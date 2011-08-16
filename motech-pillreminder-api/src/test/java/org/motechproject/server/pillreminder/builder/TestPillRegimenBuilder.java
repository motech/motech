package org.motechproject.server.pillreminder.builder;

import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.PillRegimen;

import java.util.HashSet;
import java.util.Set;

public class TestPillRegimenBuilder {

    private PillRegimen pillRegimen = new PillRegimen();


    public TestPillRegimenBuilder withDosages(Set<Dosage> dosages) {
        pillRegimen.setDosages(dosages);
        return this;
    }

    public TestPillRegimenBuilder withExternalId(String externalId) {
        pillRegimen.setExternalId(externalId);
        return this;
    }

    public TestPillRegimenBuilder withReminderRepeatWindowInHours(int reminderRepeatWindowInHours) {
        pillRegimen.setReminderRepeatWindowInHours(reminderRepeatWindowInHours);
        return this;
    }

    public TestPillRegimenBuilder withReminderRepeatIntervalInMinutes(int reminderRepeatIntervalInMinutes) {
        pillRegimen.setReminderRepeatIntervalInMinutes(reminderRepeatIntervalInMinutes);
        return this;
    }

    public PillRegimen build() {
        return pillRegimen;
    }

    public static TestPillRegimenBuilder newPillRegimen() {
        return new TestPillRegimenBuilder();
    }

    public TestPillRegimenBuilder withSingleDosage(final Dosage dosage) {
        pillRegimen.setDosages(new HashSet<Dosage>() {{
            add(dosage);
        }});
        return this;
    }
}
