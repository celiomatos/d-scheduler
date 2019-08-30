--liquibase formatted sql

--changeset celio:126

INSERT INTO quartz.scheduler_job_info (job_cron_expression, job_enable, job_group, job_name, job_cron, job_repeat_time)
	VALUES ('10 20 9,15,20 ? * *', TRUE, 'default-group', 'job-pagamento-mes-atual', TRUE, NULL);

--rollback delete from quartz.scheduler_job_info where job_name = 'job-a';

--changeset celio:127

INSERT INTO quartz.scheduler_job_info (job_cron_expression, job_enable, job_group, job_name, job_cron, job_repeat_time)
	VALUES ('20 50 */5 ? * *', TRUE, 'default-group', 'job-pagamento-mes-anterior', TRUE, NULL);

--rollback delete from quartz.scheduler_job_info where job_name = 'job-b';

--changeset celio:206

INSERT INTO quartz.scheduler_job_info (job_cron_expression, job_enable, job_group, job_name, job_cron, job_repeat_time)
	VALUES ('30 10 */2 ? * *', TRUE, 'default-group', 'job-empenho-ano-atual', TRUE, NULL);

--rollback delete from quartz.scheduler_job_info where job_name = 'job-b';

--changeset celio:207

INSERT INTO quartz.scheduler_job_info (job_cron_expression, job_enable, job_group, job_name, job_cron, job_repeat_time)
	VALUES ('40 40 * ? * *', TRUE, 'default-group', 'job-empenho-ano-anterior', TRUE, NULL);

--rollback delete from quartz.scheduler_job_info where job_name = 'job-b';

--changeset celio:scheduler-050

INSERT INTO quartz.scheduler_job_info (job_cron_expression, job_enable, job_group, job_name, job_cron, job_repeat_time)
	VALUES ('10 20 * ? * *', TRUE, 'default-group', 'job-email-alert', TRUE, NULL);

--rollback delete from quartz.scheduler_job_info where job_name = 'job-email-alert';

--changeset celio:scheduler-051

INSERT INTO quartz.scheduler_job_info (job_cron_expression, job_enable, job_group, job_name, job_cron, job_repeat_time)
	VALUES ('10 10 10 ? * *', TRUE, 'default-group', 'job-email-pagamento', TRUE, NULL);

--rollback delete from quartz.scheduler_job_info where job_name = 'job-email-pagamento';
