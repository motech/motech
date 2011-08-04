package org.motechproject.server.pillreminder.builder.test;

import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.domain.Dosage;

import java.util.Date;

public class DosageBuilder {
    private Dosage dosage = new Dosage();

    public DosageBuilder withLastTakenDate(Date lastTakenDate) {
        dosage.setLastTakenDate(lastTakenDate);
        return this;
    }

    public DosageBuilder withId(String id) {
        dosage.setId(id);
        return this;
    }

    public DosageBuilder withDosageTime(Time dosageTime) {
        dosage.setDosageTime(dosageTime);
        return this;
    }

    public Dosage build() {
        return dosage;
    }
    public static DosageBuilder newDosage() {
        return new DosageBuilder();
    }

}
