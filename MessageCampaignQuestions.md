# Message Campaign Questions #

## Questions and concerns regarding the message campaign module ##



---


Why do offset campaigns have a field for maximum duration, but cron based do not? Offset campaigns are scheduled as a single RunOnceSchedulableJob (repeating campaigns schedule multiple RunOnceSchedulableJobs). Cron are repeating and scheduled as a CronSchedulableJob with a null end date. It seems that the current implementation of offset campaigns has no use for a maximum duration field and cron based may. I do not see an indication that Offset campaigns are repeating. Is this wrong?



---


Should there be a way to specify maximum number of messages? This is not borne out in the code if so.