package org.motechproject.server.pillreminder.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;
import org.springframework.util.CollectionUtils;

import java.util.*;

@TypeDiscriminator("doc.type === 'PILLREGIMEN'")
public class PillRegimen extends MotechAuditableDataObject {

    @JsonProperty("type")
    private String type = "PILLREGIMEN";
    private String externalId;
    private int reminderRepeatWindowInHours;
    private int reminderRepeatIntervalInMinutes;
    private Set<Dosage> dosages;

    public PillRegimen() {
    }

    public PillRegimen(String externalId, int reminderRepeatWindowInHours, int reminderRepeatIntervalInMinutes, Set<Dosage> dosages) {
        this.externalId = externalId;
        this.reminderRepeatWindowInHours = reminderRepeatWindowInHours;
        this.reminderRepeatIntervalInMinutes = reminderRepeatIntervalInMinutes;
        this.dosages = dosages;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Set<Dosage> getDosages() {
        return dosages;
    }

    public void setDosages(Set<Dosage> dosages) {
        this.dosages = dosages;
    }

    public int getReminderRepeatWindowInHours() {
        return reminderRepeatWindowInHours;
    }

    public void setReminderRepeatWindowInHours(int reminderRepeatWindowInHours) {
        this.reminderRepeatWindowInHours = reminderRepeatWindowInHours;
    }

    public int getReminderRepeatIntervalInMinutes() {
        return reminderRepeatIntervalInMinutes;
    }

    public void setReminderRepeatIntervalInMinutes(int reminderRepeatIntervalInMinutes) {
        this.reminderRepeatIntervalInMinutes = reminderRepeatIntervalInMinutes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void validate() {
        for (Dosage dosage : dosages) {
            dosage.validate();
        }
    }

    public Dosage getDosage(String dosageId) {
        for (Dosage dosage : dosages)
            if (dosage.getId().equals(dosageId)) return dosage;
        return null;
    }

    public Dosage getPreviousDosage(Dosage currentDosage) {
        List<Dosage> allDosages = getSortedDosages();
        if (allDosages == null) return null;
        int currentDosageIndex = allDosages.indexOf(currentDosage);
        return currentDosageIndex == 0 ? allDosages.get(allDosages.size() - 1) : allDosages.get(currentDosageIndex - 1);
    }

    public Dosage getNextDosage(Dosage currentDosage) {
        List<Dosage> allDosages = getSortedDosages();
        if (allDosages == null) return null;
        int currentDosageIndex = allDosages.indexOf(currentDosage);
        return currentDosageIndex == allDosages.size() - 1 ? allDosages.get(0) : allDosages.get(currentDosageIndex + 1);
    }

    private List<Dosage> getSortedDosages() {
        if (CollectionUtils.isEmpty(dosages))
            return null;
        List<Dosage> sortedDosages = new ArrayList<Dosage>(dosages);
        Collections.sort(sortedDosages, new Comparator<Dosage>() {
            @Override
            public int compare(Dosage d1, Dosage d2) {
                Date today = new Date();
                Date d1TimeOfDate = d1.getStartTime().getTimeOfDate(today);
                Date d2TimeOfDate = d2.getStartTime().getTimeOfDate(today);
                return d1TimeOfDate.compareTo(d2TimeOfDate);
            }
        });
        return sortedDosages;
    }
}
