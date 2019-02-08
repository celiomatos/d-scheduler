package br.com.dscheduler.service;

import br.com.dscheduler.model.SchedulerJobInfo;

public interface SchedulerService {

    void startAllSchedulers();

    boolean scheduleNewJob(SchedulerJobInfo jobInfo);

    boolean updateScheduleJob(SchedulerJobInfo jobInfo);

    boolean unScheduleJob(String jobName);

    boolean deleteJob(SchedulerJobInfo jobInfo);

    boolean pauseJob(SchedulerJobInfo jobInfo);

    boolean resumeJob(SchedulerJobInfo jobInfo);

    boolean startJobNow(SchedulerJobInfo jobInfo);
}
