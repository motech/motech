-- Changing column 'VALUE' from VARCHAR(500) to VARCHAR(20000)

alter table "MOTECH_TASKS_TASKERROR" alter column "MESSAGE" type varchar(20000);
