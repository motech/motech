-- adds nonEditable column ---

ALTER TABLE Field add nonEditable bit(1) NOT NULL default 0;
