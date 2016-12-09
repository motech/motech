--Updates triggerName field in the MOTECH_TASKS_TASKACTIVITY.

--In the PSQL column triggerName have to be added manually.
ALTER TABLE "MOTECH_TASKS_TASKACTIVITY" DROP COLUMN IF EXISTS "triggerName";
ALTER TABLE "MOTECH_TASKS_TASKACTIVITY" ADD COLUMN "triggerName" varchar(255) DEFAULT NULL;

UPDATE "MOTECH_TASKS_TASKACTIVITY" as "dest"
SET "triggerName" = "src"."displayName"
FROM (SELECT
        "trigger"."id", "trigger"."displayName", "task"."id" as task
    FROM
        "MOTECH_TASKS_TASKTRIGGERINFORMATION" "trigger", "MOTECH_TASKS_TASK" "task"
    WHERE
        ROW("trigger"."id" , "task"."id") IN (SELECT
                "trigger_id_OID", "id"
            FROM
                "MOTECH_TASKS_TASK")) AS "src"
WHERE "src"."task" = "dest"."task";