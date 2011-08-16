package org.motechproject.server.pillreminder.builder;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.domain.Dosage;

public class TestDosageBuilder {
    private Dosage dosage = new Dosage();

    public TestDosageBuilder withResponseLastCapturedDate(LocalDate responseLastCapturedDate) {
        dosage.setResponseLastCapturedDate(responseLastCapturedDate);
        return this;
    }

    public TestDosageBuilder withId(String id) {
        dosage.setId(id);
        return this;
    }

    public TestDosageBuilder withDosageTime(Time dosageTime) {
        dosage.setDosageTime(dosageTime);
        return this;
    }

    public Dosage build() {
        return dosage;
    }
    public static TestDosageBuilder newDosage() {
        return new TestDosageBuilder();
    }

}
