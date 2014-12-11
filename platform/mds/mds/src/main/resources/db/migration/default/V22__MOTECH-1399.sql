-- adds crudEvents columns ---

ALTER TABLE "Tracking" ADD "allowCreateEvent" boolean NOT NULL DEFAULT false;
ALTER TABLE "Tracking" ADD "allowDeleteEvent" boolean NOT NULL DEFAULT false;
ALTER TABLE "Tracking" ADD "allowUpdateEvent" boolean NOT NULL DEFAULT false;
