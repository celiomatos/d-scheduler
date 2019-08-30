package br.com.dscheduler.service;

import br.com.dscheduler.component.JobScheduleCreator;
import br.com.dscheduler.job.*;
import br.com.dscheduler.model.SchedulerJobInfo;
import br.com.dscheduler.repository.SchedulerRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@Service
public class SchedulerService {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private SchedulerRepository schedulerRepository;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private JobScheduleCreator scheduleCreator;

    /**
     *
     */
    public void startAllSchedulers() {
        List<SchedulerJobInfo> jobInfoList = schedulerRepository.findAll();

        if (!jobInfoList.isEmpty()) {
            jobInfoList.forEach(jobInfo -> {
                if (jobInfo.isEnable()) {
                    scheduleNewJob(jobInfo);
                } else {
                    deleteJob(jobInfo);
                }
            });
        }
    }

    /**
     * @param jobInfo
     * @return
     */
    public boolean scheduleNewJob(SchedulerJobInfo jobInfo) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobKey jobKey = new JobKey(jobInfo.getName(), jobInfo.getGroup());

            if (scheduler.checkExists(jobKey)) {
                deleteJob(jobInfo);
            }

            JobDetail jobDetail = getJobDetail(jobInfo);
            if (jobDetail != null) {
                Trigger trigger = getTrigger(jobInfo);

                scheduler.scheduleJob(jobDetail, trigger);

                if (jobInfo.getId() == null) {
                    schedulerRepository.save(jobInfo);
                }
            }
            return true;
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * @param jobInfo
     * @return
     */
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


    /**
     * @param jobName
     * @return
     */
    public boolean unScheduleJob(String jobName) {
        try {
            return schedulerFactoryBean.getScheduler().unscheduleJob(new TriggerKey(jobName));
        } catch (SchedulerException e) {
            log.error("Failed to un-schedule job - {}", jobName, e);
            return false;
        }
    }

    /**
     * @param jobInfo
     * @return
     */
    public boolean deleteJob(SchedulerJobInfo jobInfo) {
        try {
            JobKey jobKey = new JobKey(jobInfo.getName(), jobInfo.getGroup());
            return schedulerFactoryBean.getScheduler().deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error("Failed to delete job - {}", jobInfo.getName(), e);
            return false;
        }
    }

    /**
     * @param jobInfo
     * @return
     */
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

    /**
     * @param jobInfo
     * @return
     */
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

    /**
     * @param jobInfo
     * @return
     */
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

    /**
     * @param jobInfo
     * @return
     */
    private JobDetail getJobDetail(SchedulerJobInfo jobInfo) {

        JobKey jobKey = new JobKey(jobInfo.getName(), jobInfo.getGroup());

        log.info("{} ::: ", jobInfo.isCron() ? jobInfo.getExpression() : jobInfo.getRepeatTime());
        JobDetail jobDetail = null;

        if (jobKey.getName().equalsIgnoreCase("job-pagamento-mes-atual")) {
            scheduleCreator.createJob(PagamentoMesAtualJob.class, false, context, jobKey.getName(), jobKey.getGroup());
            jobDetail = JobBuilder.newJob(PagamentoMesAtualJob.class).withIdentity(jobKey).build();
        } else if (jobKey.getName().equalsIgnoreCase("job-pagamento-mes-anterior")) {
            scheduleCreator.createJob(PagamentoMesAnteriorJob.class, false, context, jobKey.getName(), jobKey.getGroup());
            jobDetail = JobBuilder.newJob(PagamentoMesAnteriorJob.class).withIdentity(jobKey).build();
        } else if (jobKey.getName().equalsIgnoreCase("job-empenho-ano-atual")) {
            scheduleCreator.createJob(EmpenhoAnoAtualJob.class, false, context, jobKey.getName(), jobKey.getGroup());
            jobDetail = JobBuilder.newJob(EmpenhoAnoAtualJob.class).withIdentity(jobKey).build();
        } else if (jobKey.getName().equalsIgnoreCase("job-empenho-ano-anterior")) {
            scheduleCreator.createJob(EmpenhoAnoAnteriorJob.class, false, context, jobKey.getName(), jobKey.getGroup());
            jobDetail = JobBuilder.newJob(EmpenhoAnoAnteriorJob.class).withIdentity(jobKey).build();
        } else if (jobKey.getName().equalsIgnoreCase("job-email-alert")) {
            scheduleCreator.createJob(MailAlertJob.class, false, context, jobKey.getName(), jobKey.getGroup());
            jobDetail = JobBuilder.newJob(MailAlertJob.class).withIdentity(jobKey).build();
        } else if (jobKey.getName().equalsIgnoreCase("job-email-pagamento")) {
            scheduleCreator.createJob(MailPaymentJob.class, false, context, jobKey.getName(), jobKey.getGroup());
            jobDetail = JobBuilder.newJob(MailPaymentJob.class).withIdentity(jobKey).build();
        }
        return jobDetail;
    }

    /**
     * @param jobInfo
     * @return
     */
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
