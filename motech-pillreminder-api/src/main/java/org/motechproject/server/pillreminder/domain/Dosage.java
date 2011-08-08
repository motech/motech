package org.motechproject.server.pillreminder.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;

import java.util.*;

public class Dosage {
    private String id;
    private Time dosageTime;
    private LocalDate responseLastCapturedDate;
    private Set<Medicine> medicines;

    public Dosage() {
    }

    public Dosage(Time dosageTime, Set<Medicine> medicines) {
        this.id = UUID.randomUUID().toString();
        this.dosageTime = dosageTime;
        this.medicines = medicines;
    }

    public Set<Medicine> getMedicines() {
        return medicines;
    }

    public void setMedicines(Set<Medicine> medicines) {
        this.medicines = medicines;
    }

    public Time getDosageTime() {
        return dosageTime;
    }

    public void setDosageTime(Time dosageTime) {
        this.dosageTime = dosageTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getResponseLastCapturedDate() {
        return responseLastCapturedDate;
    }

    public void setResponseLastCapturedDate(LocalDate responseLastCapturedDate) {
        this.responseLastCapturedDate = responseLastCapturedDate;
    }

    @JsonIgnore
    public boolean isTodaysDosageResponseCaptured() {
        return getResponseLastCapturedDate() != null && getResponseLastCapturedDate().equals(DateUtil.today());
    }

    @JsonIgnore
    public LocalDate getStartDate() {
        List<Medicine> sortedList = new ArrayList<Medicine>(medicines);
        Collections.sort(sortedList, new Comparator<Medicine>() {
            @Override
            public int compare(Medicine o1, Medicine o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
        return sortedList.isEmpty() ? null : sortedList.get(0).getStartDate();
    }

    @JsonIgnore
    public LocalDate getEndDate() {
        Set<Medicine> medicinesWithNonNullEndDate = getMedicinesWithNonNullEndDate();
        if (medicinesWithNonNullEndDate.isEmpty()) return null;

        List<Medicine> sortedList = new ArrayList<Medicine>(medicinesWithNonNullEndDate);
        Collections.sort(sortedList, new Comparator<Medicine>() {
            @Override
            public int compare(Medicine o1, Medicine o2) {
                return o2.getEndDate().compareTo(o1.getEndDate());
            }
        });
        return sortedList.isEmpty() ? null : sortedList.get(0).getEndDate();
    }

    private Set<Medicine> getMedicinesWithNonNullEndDate() {
        Set<Medicine> medicinesWithNonNullEndDate = new HashSet<Medicine>();
        for (Medicine medicine : medicines) {
            if (medicine.getEndDate() != null)
                medicinesWithNonNullEndDate.add(medicine);
        }
        return medicinesWithNonNullEndDate;
    }

    public void validate() {
        for (Medicine medicine : getMedicines())
            medicine.validate();
    }

    public void updateResponseLastCapturedDate() {
        responseLastCapturedDate = DateUtil.today();
    }
}