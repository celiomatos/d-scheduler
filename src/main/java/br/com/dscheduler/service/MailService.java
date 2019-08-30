package br.com.dscheduler.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
@FeignClient("d-mail-service")
public interface MailService {

    @GetMapping("/mail/d-alert")
    void sendDAlertMessage();

    @GetMapping("/mail/d-payment")
    void sendPaymentMessage();
}
