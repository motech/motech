-- change default value of the MDS CRUD events and add new flag ---

ALTER TABLE Tracking ALTER allowCreateEvent SET DEFAULT 1;
ALTER TABLE Tracking ALTER allowDeleteEvent SET DEFAULT 1;
ALTER TABLE Tracking ALTER allowUpdateEvent SET DEFAULT 1;
ALTER TABLE Tracking add modifiedByUser bit(1) NOT NULL default 0;
