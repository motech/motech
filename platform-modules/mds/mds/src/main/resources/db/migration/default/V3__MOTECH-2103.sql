DROP TABLE IF EXISTS "UserPreferences_selectedFields";
DROP TABLE IF EXISTS "UserPreferences_unselectedFields";

CREATE TABLE IF NOT EXISTS "UserPreferences_selectedFields" (
  "className_OID" varchar(255) NOT NULL,
  "username_OID" varchar(255) NOT NULL,
  "selectedField" bigint,
  PRIMARY KEY ("className_OID", "username_OID", "selectedField"),
  CONSTRAINT "UserPreferences_selectedFields_FK1" FOREIGN KEY ("username_OID", "className_OID") REFERENCES "UserPreferences" ("username", "className"),
  CONSTRAINT "UserPreferences_selectedFields_FK2" FOREIGN KEY ("selectedField") REFERENCES "Field" ("id") ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "UserPreferences_unselectedFields" (
  "className_OID" varchar(255) NOT NULL,
  "username_OID" varchar(255) NOT NULL,
  "unselectedField" bigint,
  PRIMARY KEY ("className_OID", "username_OID", "unselectedField"),
  CONSTRAINT "UserPreferences_unselectedFields_FK1" FOREIGN KEY ("username_OID", "className_OID") REFERENCES "UserPreferences" ("username", "className"),
  CONSTRAINT "UserPreferences_unselectedFields_FK2" FOREIGN KEY ("unselectedField") REFERENCES "Field" ("id") ON DELETE CASCADE
);