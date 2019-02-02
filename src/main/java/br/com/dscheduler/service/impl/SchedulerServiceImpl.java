package br.com.dscheduler.service.impl;

import br.com.dscheduler.component.JobScheduleCreator;
import br.com.dscheduler.job.JobA;
import br.com.dscheduler.job.JobB;
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
        if (jobInfoList != null) {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            jobInfoList.forEach(jobInfo -> {
                try {
                    if (jobInfo.isEnable()) {
                        JobKey jobKey = new JobKey(jobInfo.getName(), jobInfo.getGroup());
                        JobDetail jobDetail = newJobDetail(jobInfo);

                        if (!scheduler.checkExists(jobDetail.getKey())) {
                            createJobDetail(jobKey);

                            Trigger trigger;

                            if (jobInfo.isCron() && CronExpression.isValidExpression(jobInfo.getExpression())) {
                                trigger = scheduleCreator.createCronTrigger(jobInfo.getName(), new Date(),
                                        jobInfo.getExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                            } else {
                                trigger = scheduleCreator.createSimpleTrigger(jobInfo.getName(), new Date(),
                                        jobInfo.getRepeatTime(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                            }

                            scheduler.scheduleJob(jobDetail, trigger);

                        }
                    }
                } catch (SchedulerException e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }

    @Override
    public void scheduleNewJob(SchedulerJobInfo jobInfo) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobKey jobKey = new JobKey(jobInfo.getName(), jobInfo.getGroup());
            JobDetail jobDetail = newJobDetail(jobInfo);

            if (!scheduler.checkExists(jobDetail.getKey())) {

                createJobDetail(jobKey);

                Trigger trigger;
                if (jobInfo.isCron()) {
                    trigger = scheduleCreator.createCronTrigger(jobInfo.getName(), new Date(), jobInfo.getExpression(),
                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                } else {
                    trigger = scheduleCreator.createSimpleTrigger(jobInfo.getName(), new Date(), jobInfo.getRepeatTime(),
                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                }

                scheduler.scheduleJob(jobDetail, trigger);
                schedulerRepository.save(jobInfo);
            } else {
                log.error("scheduleNewJobRequest.jobAlreadyExist");
            }
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void updateScheduleJob(SchedulerJobInfo jobInfo) {
        Trigger newTrigger;
        if (jobInfo.isCron()) {
            newTrigger = scheduleCreator.createCronTrigger(jobInfo.getName(), new Date(), jobInfo.getExpression(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        } else {
            newTrigger = scheduleCreator.createSimpleTrigger(jobInfo.getName(), new Date(), jobInfo.getRepeatTime(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        }
        try {
            schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobInfo.getName()), newTrigger);
            schedulerRepository.save(jobInfo);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
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
            return schedulerFactoryBean.getScheduler().deleteJob(new JobKey(jobInfo.getName(), jobInfo.getGroup()));
        } catch (SchedulerException e) {
            log.error("Failed to delete job - {}", jobInfo.getName(), e);
            return false;
        }
    }

    @Override
    public boolean pauseJob(SchedulerJobInfo jobInfo) {
        try {
            schedulerFactoryBean.getScheduler().pauseJob(new JobKey(jobInfo.getName(), jobInfo.getGroup()));
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to pause job - {}", jobInfo.getName(), e);
            return false;
        }
    }

    @Override
    public boolean resumeJob(SchedulerJobInfo jobInfo) {
        try {
            schedulerFactoryBean.getScheduler().resumeJob(new JobKey(jobInfo.getName(), jobInfo.getGroup()));
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to resume job - {}", jobInfo.getName(), e);
            return false;
        }
    }

    @Override
    public boolean startJobNow(SchedulerJobInfo jobInfo) {
        try {
            schedulerFactoryBean.getScheduler().triggerJob(new JobKey(jobInfo.getName(), jobInfo.getGroup()));
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to start new job - {}", jobInfo.getName(), e);
            return false;
        }
    }

    private JobDetail newJobDetail(SchedulerJobInfo jobInfo) {

        JobKey jobKey = new JobKey(jobInfo.getName(), jobInfo.getGroup());

        switch (jobKey.getName()) {
            case "job-a": {
                return JobBuilder.newJob(JobA.class)
                        .withIdentity(jobKey)
                        .usingJobData("command", jobInfo.getCommand())
                        .build();
            }
            case "job-b": {
                return JobBuilder.newJob(JobB.class)
                        .withIdentity(jobKey)
                        .usingJobData("command", jobInfo.getCommand())
                        .build();
            }
            default:
                return null;
        }
    }

    private void createJobDetail(JobKey jobKey) {
        switch (jobKey.getName()) {
            case "job-a": {
                scheduleCreator.createJob(JobA.class, false, context, jobKey.getName(), jobKey.getGroup());
            }
            case "job-b": {
                scheduleCreator.createJob(JobB.class, false, context, jobKey.getName(), jobKey.getGroup());
            }
            default:
                log.error("Job {} not found", jobKey.getName());
        }
    }
}
