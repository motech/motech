-- add new flag to RestOptions---

ALTER TABLE "RestOptions" ADD "modifiedByUser" boolean NOT NULL DEFAULT false;
