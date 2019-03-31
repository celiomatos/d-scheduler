package br.com.dscheduler.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @GetMapping
    public Date getDste() {
        return new Date();
    }
}
