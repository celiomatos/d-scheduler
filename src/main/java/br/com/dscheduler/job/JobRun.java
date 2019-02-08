package br.com.dscheduler.job;

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
public class JobRun extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        String token = context.getJobDetail().getJobDataMap().getString("token");
        String command = context.getJobDetail().getJobDataMap().getString("command");

        log.info("Job {} is running ::: " + new Date(), context.getJobDetail().getKey().getName());
        log.info(token + " :::::: " + command);

        Request request = new Request.Builder()
                .header("Authorization", token)
                .url(command)
                .build();

        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();
            log.info(response.body().string());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
