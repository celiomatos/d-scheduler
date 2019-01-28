package br.com.dscheduler.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "scheduler_job_info", schema = "quartz")
public class SchedulerJobInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "job_name", unique = true, nullable = false)
    private String name;

    @Column(name = "job_group", nullable = false)
    private String group;

    @Column(name = "job_enable", nullable = false)
    private boolean enable;

    @Column(name = "job_cron_expression")
    private String cronExpression;

    @Column(name = "job_repeat_time")
    private Long repeatTime;

    @Column(name = "job_cron", nullable = false)
    private boolean cron;
}
