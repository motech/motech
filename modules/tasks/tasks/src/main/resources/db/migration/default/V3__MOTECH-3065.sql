-- Changing column 'MESSAGE' from VARCHAR(255) to TEXT

alter table "MOTECH_TASKS_TASKERROR" alter column "message" type TEXT;

alter table "MOTECH_TASKS_TASKERROR__HISTORY" alter column "message" type TEXT;

alter table "MOTECH_TASKS_TASKERROR__TRASH" alter column "message" type TEXT;
