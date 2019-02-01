package br.com.dscheduler.job;

import br.com.dscheduler.config.ClientHttp;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.IOException;
import java.util.Date;

@Slf4j
@DisallowConcurrentExecution
public class SampleJobA extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Job A is running ::: " + new Date());
//        new ClientHttp().pagamentoAtual();
        okMethod();
    }

    public void okMethod(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization","bearer b185e947-5709-4bfa-af73-3ae930166345")
                .url("http://localhost:10101/scraper")
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            log.warn(e.getMessage());
        }

    }
}
