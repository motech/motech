-- adds nonEditable column ---

ALTER TABLE Tracking add nonEditable bit(1) NOT NULL default 0;
