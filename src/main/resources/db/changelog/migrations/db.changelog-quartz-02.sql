--liquibase formatted sql

--changeset celio:scheduler-050

INSERT INTO quartz.scheduler_job_info (job_cron_expression, job_enable, job_group, job_name, job_cron, job_repeat_time, job_command)
	VALUES ('10 20 * ? * *', TRUE, 'default-group', 'job-email-alert', TRUE, NULL, 'http://d-server-mail:8085/mail/d-alert');

--rollback delete from quartz.scheduler_job_info where job_name = 'job-email-alert';

--changeset celio:scheduler-051

INSERT INTO quartz.scheduler_job_info (job_cron_expression, job_enable, job_group, job_name, job_cron, job_repeat_time, job_command)
	VALUES ('10 10 10 ? * *', TRUE, 'default-group', 'job-email-pagamento', TRUE, NULL, 'http://d-server-mail:8085/mail/d-payment');

--rollback delete from quartz.scheduler_job_info where job_name = 'job-email-pagamento';
