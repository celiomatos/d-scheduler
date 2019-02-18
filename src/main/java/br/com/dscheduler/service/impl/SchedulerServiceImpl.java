package br.com.dscheduler.service.impl;

import br.com.dscheduler.component.JobScheduleCreator;
import br.com.dscheduler.job.JobRun;
import br.com.dscheduler.model.SchedulerJobInfo;
import br.com.dscheduler.repository.SchedulerRepository;
import br.com.dscheduler.service.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Transactional
@Service
public class SchedulerServiceImpl implements SchedulerService {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private SchedulerRepository schedulerRepository;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private JobScheduleCreator scheduleCreator;

    @Override
    public void startAllSchedulers() {
        List<SchedulerJobInfo> jobInfoList = schedulerRepository.findAll();

        if (!jobInfoList.isEmpty()) {
            jobInfoList.forEach(jobInfo -> {
                if (jobInfo.isEnable()) {
                    scheduleNewJob(jobInfo);
                }
            });
        }
    }

    @Override
    public boolean scheduleNewJob(SchedulerJobInfo jobInfo) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobDetail jobDetail = getJobDetail(jobInfo);

            JobKey jobKey = jobDetail.getKey();

            if (scheduler.checkExists(jobKey)) {
                deleteJob(jobInfo);
            }
            scheduleCreator.createJob(JobRun.class, false, context, jobKey.getName(), jobKey.getGroup());

            Trigger trigger = getTrigger(jobInfo);

            scheduler.scheduleJob(jobDetail, trigger);

            if (jobInfo.getId() == null) {
                schedulerRepository.save(jobInfo);
            }

            return true;
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateScheduleJob(SchedulerJobInfo jobInfo) {

        Trigger newTrigger = getTrigger(jobInfo);

        try {
            schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobInfo.getName()), newTrigger);
            schedulerRepository.save(jobInfo);
            return true;
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }


    @Override
    public boolean unScheduleJob(String jobName) {
        try {
            return schedulerFactoryBean.getScheduler().unscheduleJob(new TriggerKey(jobName));
        } catch (SchedulerException e) {
            log.error("Failed to un-schedule job - {}", jobName, e);
            return false;
        }
    }

    @Override
    public boolean deleteJob(SchedulerJobInfo jobInfo) {
        try {
            JobKey jobKey = new JobKey(jobInfo.getName(), jobInfo.getGroup());
            return schedulerFactoryBean.getScheduler().deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error("Failed to delete job - {}", jobInfo.getName(), e);
            return false;
        }
    }

    @Override
    public boolean pauseJob(SchedulerJobInfo jobInfo) {
        try {
            JobKey jobKey = new JobKey(jobInfo.getName(), jobInfo.getGroup());
            schedulerFactoryBean.getScheduler().pauseJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to pause job - {}", jobInfo.getName(), e);
            return false;
        }
    }

    @Override
    public boolean resumeJob(SchedulerJobInfo jobInfo) {
        try {
            JobKey jobKey = new JobKey(jobInfo.getName(), jobInfo.getGroup());
            schedulerFactoryBean.getScheduler().resumeJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to resume job - {}", jobInfo.getName(), e);
            return false;
        }
    }

    @Override
    public boolean startJobNow(SchedulerJobInfo jobInfo) {
        try {
            JobKey jobKey = new JobKey(jobInfo.getName(), jobInfo.getGroup());
            schedulerFactoryBean.getScheduler().triggerJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to start new job - {}", jobInfo.getName(), e);
            return false;
        }
    }

    private JobDetail getJobDetail(SchedulerJobInfo jobInfo) {

        JobKey jobKey = new JobKey(jobInfo.getName(), jobInfo.getGroup());

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("command", jobInfo.getCommand());
        jobDataMap.put("token", "bearer " + jobInfo.getToken());

        log.info("{} ::: " + jobInfo.getCommand(), jobInfo.isCron() ? jobInfo.getExpression() : jobInfo.getRepeatTime());

        return JobBuilder.newJob(JobRun.class)
                .withIdentity(jobKey)
                .usingJobData(jobDataMap)
                .build();
    }

    private Trigger getTrigger(SchedulerJobInfo jobInfo) {

        Trigger trigger;

        if (jobInfo.isCron() && CronExpression.isValidExpression(jobInfo.getExpression())) {
            trigger = scheduleCreator.createCronTrigger(jobInfo.getName(), null,
                    jobInfo.getExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        } else {
            trigger = scheduleCreator.createSimpleTrigger(jobInfo.getName(), null,
                    jobInfo.getRepeatTime(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        }

        return trigger;
    }
}
