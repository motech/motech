-- clean up auditing columns ---

ALTER TABLE "Field" DROP COLUMN "tracked";

ALTER TABLE "Tracking" DROP COLUMN "allowCreate";
ALTER TABLE "Tracking" DROP COLUMN "allowRead";
ALTER TABLE "Tracking" DROP COLUMN "allowUpdate";
ALTER TABLE "Tracking" DROP COLUMN "allowDelete";
