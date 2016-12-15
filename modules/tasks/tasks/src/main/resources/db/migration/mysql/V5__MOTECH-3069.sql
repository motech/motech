--Updates triggerName field in the MOTECH_TASKS_TASKACTIVITY.

SET SQL_SAFE_UPDATES = 0;
UPDATE `MOTECH_TASKS_TASKACTIVITY` `dest`,
    (SELECT
        `trigger`.`id`, `trigger`.`displayName`, `task`.`id` AS 'taskId'
    FROM
        `MOTECH_TASKS_TASKTRIGGERINFORMATION` `trigger`, `MOTECH_TASKS_TASK` `task`
    WHERE
        ROW(`trigger`.`id` , `task`.`id`) IN (SELECT
                `trigger_id_OID`, `id`
            FROM
                `MOTECH_TASKS_TASK`)) `src`
SET
    `dest`.`triggerName` = `src`.`displayName`
WHERE
    `dest`.`task` = `src`.`taskId`;
SET SQL_SAFE_UPDATES = 1;