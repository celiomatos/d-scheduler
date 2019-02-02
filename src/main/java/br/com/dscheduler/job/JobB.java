package br.com.dscheduler.job;

import br.com.dscheduler.config.ClientHttp;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

@Slf4j
@DisallowConcurrentExecution
public class JobB extends QuartzJobBean {


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        new ClientHttp().get(context);
    }
}
