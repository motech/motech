-- add new flag to RestOptions---

ALTER TABLE RestOptions add modifiedByUser bit(1) NOT NULL default 0;
