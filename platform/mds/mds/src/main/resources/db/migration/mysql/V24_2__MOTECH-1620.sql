-- adds nonDisplayable column ---

ALTER TABLE Field add nonDisplayable bit(1) NOT NULL default 0;
