package br.com.dscheduler.job;

import br.com.dscheduler.config.ClientHttp;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
@DisallowConcurrentExecution
public class JobRun extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        new ClientHttp().get(context);
    }

}
