-- adds crudEvents columns ---

ALTER TABLE Tracking add allowCreateEvent bit(1) NOT NULL default 0;
ALTER TABLE Tracking add allowDeleteEvent bit(1) NOT NULL default 0;
ALTER TABLE Tracking add allowUpdateEvent bit(1) NOT NULL default 0;
