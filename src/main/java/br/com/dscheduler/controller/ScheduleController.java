package br.com.dscheduler.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Api("scraper")
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @ApiOperation("get mehodo")
    @GetMapping
    public Date getDste(){
        return new Date();
    }
}
