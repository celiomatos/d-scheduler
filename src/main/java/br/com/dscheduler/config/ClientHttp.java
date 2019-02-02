package br.com.dscheduler.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

@Slf4j
public class ClientHttp {

    public void get(JobExecutionContext context) {

        JobKey jobKey = context.getJobDetail().getKey();
        log.info("Job {} start ::: " + new Date(), jobKey.getName());

        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", "bearer b185e947-5709-4bfa-af73-3ae930166345")
                .url(jobDataMap.getString("command"))
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            log.warn(e.getMessage());
        }

        log.info("Job {} stop ::: " + new Date(), jobKey.getName());
    }
}
