package br.com.dscheduler.job;

import br.com.dscheduler.service.ScraperService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

@Slf4j
@DisallowConcurrentExecution
public class PagamentoMesAtualJob extends QuartzJobBean {

    @Autowired
    private ScraperService scraperService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        log.info("Job {} is running ::: " + new Date(), context.getJobDetail().getKey().getName());
        scraperService.pagamentoMesAtual();
    }

}
