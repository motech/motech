package org.motechproject.server.pillreminder.builder.test;

import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.domain.Dosage;

import java.util.Date;

public class DosageBuilder {
    private Dosage dosage = new Dosage();

    public DosageBuilder withCurrentDosageDate(Date currentDosageDate) {
        dosage.setCurrentDosageDate(currentDosageDate);
        return this;
    }

    public DosageBuilder withId(String id) {
        dosage.setId(id);
        return this;
    }

    public DosageBuilder withStartTime(Time startTime) {
        dosage.setStartTime(startTime);
        return this;
    }

    public Dosage build() {
        return dosage;
    }
    public static DosageBuilder newDosage() {
        return new DosageBuilder();
    }

}
