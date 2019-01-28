package br.com.dscheduler.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class ClientHttp {

    public void pagamentoAtual() {
        try {
            URL url = new URL("http://localhost:10101/scraper");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            if (con.getResponseCode() != 200) {
                throw new RuntimeException("");
            }

            con.disconnect();
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }
}
