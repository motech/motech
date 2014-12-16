-- change default value of the MDS CRUD events and add new flag---

ALTER TABLE "Tracking" ALTER COLUMN "allowCreateEvent" SET DEFAULT true;
ALTER TABLE "Tracking" ALTER COLUMN "allowDeleteEvent" SET DEFAULT true;
ALTER TABLE "Tracking" ALTER COLUMN "allowUpdateEvent" SET DEFAULT true;
ALTER TABLE "Tracking" ADD "modifiedByUser" boolean NOT NULL DEFAULT false;
