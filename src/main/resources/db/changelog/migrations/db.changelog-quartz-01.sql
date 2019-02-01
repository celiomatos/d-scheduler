--liquibase formatted sql

--changeset celio:92

create schema if not exists quartz;

--rollback drop schema scraper;

--changeset celio:93

create table if not exists quartz.scheduler_job_info (
  job_id bigserial,
  job_cron_expression varchar(255),
  job_cron boolean NOT NULL,
  job_enable boolean NOT NULL,
  job_group varchar(255) NOT NULL,
  job_name varchar(255) NOT NULL UNIQUE,
  job_repeat_time bigint,
  primary key (job_id)
);

--rollback drop table quartz.scheduler_job_info;

--changeset celio:94

create table if not exists quartz.job_details (
    job_name character varying(128) NOT NULL,
    job_group character varying(80) NOT NULL,
    description character varying(120),
    job_class_name character varying(200) NOT NULL,
    is_durable boolean,
    is_nonconcurrent boolean,
    is_update_data boolean,
    sched_name character varying(120) DEFAULT 'TestScheduler'::character varying NOT NULL,
    requests_recovery boolean,
    job_data bytea,
	primary key (sched_name, job_name, job_group)
);

--rollback drop table quartz.job_details;

--changeset celio:95

create table if not exists quartz.triggers (
    trigger_name character varying(80) NOT NULL,
    trigger_group character varying(80) NOT NULL,
    job_name character varying(80) NOT NULL,
    job_group character varying(80) NOT NULL,
    description character varying(120),
    next_fire_time bigint,
    prev_fire_time bigint,
    priority integer,
    trigger_state character varying(16) NOT NULL,
    trigger_type character varying(8) NOT NULL,
    start_time bigint NOT NULL,
    end_time bigint,
    calendar_name character varying(80),
    misfire_instr smallint,
    job_data bytea,
    sched_name character varying(120) DEFAULT 'TestScheduler'::character varying NOT NULL,
	primary key (sched_name, trigger_name, trigger_group),
	foreign key (sched_name, job_name, job_group) references quartz.job_details(sched_name, job_name, job_group)
);

--rollback drop table quartz.triggers;

--changeset celio:96

create table if not exists quartz.simple_triggers (
    trigger_name character varying(80) NOT NULL,
    trigger_group character varying(80) NOT NULL,
    repeat_count bigint NOT NULL,
    repeat_interval bigint NOT NULL,
    times_triggered bigint NOT NULL,
    sched_name character varying(120) DEFAULT 'TestScheduler'::character varying NOT NULL,
	primary key (sched_name, trigger_name, trigger_group),
	foreign key (sched_name, trigger_name, trigger_group) references quartz.triggers(sched_name, trigger_name, trigger_group)
);

--rollback drop table quartz.simple_triggers;

--changeset celio:97

create table if not exists quartz.cron_triggers (
    trigger_name character varying(80) NOT NULL,
    trigger_group character varying(80) NOT NULL,
    cron_expression character varying(80) NOT NULL,
    time_zone_id character varying(80),
    sched_name character varying(120) DEFAULT 'TestScheduler'::character varying NOT NULL,
	primary key (sched_name, trigger_name, trigger_group),
	foreign key (sched_name, trigger_name, trigger_group) references quartz.triggers(sched_name, trigger_name, trigger_group)
);

--rollback drop table quartz.cron_triggers;

--changeset celio:98

create table if not exists quartz.simprop_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    str_prop_1 character varying(512),
    str_prop_2 character varying(512),
    str_prop_3 character varying(512),
    int_prop_1 integer,
    int_prop_2 integer,
    long_prop_1 bigint,
    long_prop_2 bigint,
    dec_prop_1 numeric(13,4),
    dec_prop_2 numeric(13,4),
    bool_prop_1 boolean,
    bool_prop_2 boolean,
	primary key (sched_name, trigger_name, trigger_group),
	foreign key (sched_name, trigger_name, trigger_group) references quartz.triggers(sched_name, trigger_name, trigger_group)
);

--rollback drop table quartz.simprop_triggers;

--changeset celio:99

create table if not exists quartz.blob_triggers (
    trigger_name character varying(80) NOT NULL,
    trigger_group character varying(80) NOT NULL,
    blob_data text,
    sched_name character varying(120) DEFAULT 'TestScheduler'::character varying NOT NULL,
	primary key (sched_name, trigger_name, trigger_group),
	foreign key (sched_name, trigger_name, trigger_group) references quartz.triggers(sched_name, trigger_name, trigger_group)
);

--rollback drop table quartz.blob_triggers;

--changeset celio:100

create table if not exists quartz.calendars (
    calendar_name character varying(80) NOT NULL,
    calendar text NOT NULL,
    sched_name character varying(120) DEFAULT 'TestScheduler'::character varying NOT NULL,
	primary key (sched_name, calendar_name)
);

--rollback drop table quartz.calendars;

--changeset celio:101

create table if not exists quartz.paused_trigger_grps (
    trigger_group character varying(80) NOT NULL,
    sched_name character varying(120) DEFAULT 'TestScheduler'::character varying NOT NULL,
	primary key (sched_name, trigger_group)
);

--rollback drop table quartz.paused_trigger_grps;

--changeset celio:102

create table if not exists quartz.fired_triggers (
    entry_id character varying(95) NOT NULL,
    trigger_name character varying(80) NOT NULL,
    trigger_group character varying(80) NOT NULL,
    instance_name character varying(80) NOT NULL,
    fired_time bigint NOT NULL,
    priority integer NOT NULL,
    state character varying(16) NOT NULL,
    job_name character varying(80),
    job_group character varying(80),
    is_nonconcurrent boolean,
    is_update_data boolean,
    sched_name character varying(120) DEFAULT 'TestScheduler'::character varying NOT NULL,
    sched_time bigint NOT NULL,
    requests_recovery boolean,
	primary key (sched_name, entry_id)
);

--rollback drop table quartz.fired_triggers;

--changeset celio:103

create table if not exists quartz.scheduler_state (
    instance_name character varying(200) NOT NULL,
    last_checkin_time bigint,
    checkin_interval bigint,
    sched_name character varying(120) DEFAULT 'TestScheduler'::character varying NOT NULL,
	primary key (sched_name, instance_name)
);

--rollback drop table quartz.scheduler_state;

--changeset celio:104

create table if not exists quartz.locks (
    lock_name character varying(40) NOT NULL,
    sched_name character varying(120) DEFAULT 'TestScheduler'::character varying NOT NULL,
	primary key (sched_name, lock_name)
);

--rollback drop table quartz.locks;

--changeset celio:105

create index if not exists job_details_sched_name_requests_recovery_idx on quartz.job_details(sched_name, requests_recovery);

--changeset celio:106

create index if not exists job_details_sched_name_job_group_idx on quartz.job_details(sched_name,job_group);

--changeset celio:107

create index if not exists blob_triggers_sched_name_trigger_name_trigger_group_idx on quartz.blob_triggers(sched_name, trigger_name, trigger_group);

--changeset celio:108

create index if not exists triggers_sched_name_job_name_job_group_idx on quartz.triggers(sched_name, job_name, job_group);

--changeset celio:109

create index if not exists triggers_sched_name_job_group_idx on quartz.triggers(sched_name, job_group);

--changeset celio:110

create index if not exists triggers_sched_name_calendar_name_idx on quartz.triggers(sched_name, calendar_name);

--changeset celio:111

create index if not exists triggers_sched_name_trigger_group_idx on quartz.triggers(sched_name, trigger_group);

--changeset celio:112

create index if not exists triggers_sched_name_trigger_state_idx on quartz.triggers(sched_name, trigger_state);

--changeset celio:113

create index if not exists triggers_sched_name_trigger_name_trigger_group_trigger_stat_idx on quartz.triggers(sched_name, trigger_name, trigger_group, trigger_state);

--changeset celio:114

create index if not exists triggers_sched_name_trigger_group_trigger_state_idx on quartz.triggers(sched_name, trigger_group, trigger_state);

--changeset celio:115

create index if not exists triggers_sched_name_next_fire_time_idx on quartz.triggers(sched_name, next_fire_time);

--changeset celio:116

create index if not exists triggers_sched_name_trigger_state_next_fire_time_idx on quartz.triggers(sched_name, trigger_state, next_fire_time);

--changeset celio:117

create index if not exists triggers_sched_name_misfire_instr_next_fire_time_idx on quartz.triggers(sched_name,misfire_instr,next_fire_time);

--changeset celio:118

create index if not exists triggers_sched_name_misfire_instr_next_fire_time_trigger_st_idx on quartz.triggers(sched_name,misfire_instr,next_fire_time,trigger_state);

--changeset celio:119

create index if not exists triggers_sched_name_misfire_instr_next_fire_time_trigger_gr_idx on quartz.triggers(sched_name, misfire_instr, next_fire_time, trigger_group, trigger_state);

--changeset celio:120

create index if not exists fired_triggers_sched_name_instance_name_idx on quartz.fired_triggers(sched_name, instance_name);

--changeset celio:121

create index if not exists fired_triggers_sched_name_instance_name_requests_recovery_idx on quartz.fired_triggers(sched_name, instance_name, requests_recovery);

--changeset celio:122

create index if not exists fired_triggers_sched_name_job_name_job_group_idx on quartz.fired_triggers(sched_name, job_name, job_group);

--changeset celio:123

create index if not exists fired_triggers_sched_name_job_group_idx on quartz.fired_triggers(sched_name, job_group);

--changeset celio:124

create index if not exists fired_triggers_sched_name_trigger_name_trigger_group_idx on quartz.fired_triggers(sched_name, trigger_name, trigger_group);

--changeset celio:125

create index if not exists fired_triggers_sched_name_trigger_group_idx on quartz.fired_triggers(sched_name, trigger_group);

--changeset celio:126

INSERT INTO quartz.scheduler_job_info (job_cron_expression, job_enable, job_group, job_name, job_cron, job_repeat_time)
	VALUES ('0 0/5 * ? * *', TRUE, 'default-group', 'job-a', TRUE, NULL);

--rollback delete from quartz.scheduler_job_info where job_name = 'job-a';

--changeset celio:127

INSERT INTO quartz.scheduler_job_info (job_cron_expression, job_enable, job_group, job_name, job_cron, job_repeat_time)
	VALUES (NULL, TRUE, 'default-group', 'job-b', FALSE, '600000');

--rollback delete from quartz.scheduler_job_info where job_name = 'job-b';
