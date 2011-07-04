package org.motechproject.server.pillreminder.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.model.Time;

import java.util.*;

public class Dosage{
    private String id;
    private Time startTime;
    private Set<Medicine> medicines;

    public Dosage() {
    }

    public Dosage(Time startTime, Set<Medicine> medicines) {
        this.id = UUID.randomUUID().toString();
        this.startTime = startTime;
        this.medicines = medicines;
    }

    public Set<Medicine> getMedicines() {
        return medicines;
    }

    public void setMedicines(Set<Medicine> medicines) {
        this.medicines = medicines;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public Date getStartDate() {
        ArrayList<Medicine> sortedList = new ArrayList<Medicine>(medicines);
        Collections.sort(sortedList, new Comparator<Medicine>() {
            @Override
            public int compare(Medicine o1, Medicine o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
        return sortedList.isEmpty() ? null : sortedList.get(0).getStartDate();
    }

    @JsonIgnore
    public Date getEndDate() {
        ArrayList<Medicine> sortedList = new ArrayList<Medicine>(medicines);
        Collections.sort(sortedList, new Comparator<Medicine>() {
            @Override
            public int compare(Medicine o1, Medicine o2) {
                return o2.getEndDate().compareTo(o1.getEndDate());
            }
        });
        return sortedList.isEmpty() ? null : sortedList.get(0).getEndDate();
    }

    public void validate() {
        for(Medicine medicine : getMedicines()) {
            medicine.validate();
        }
    }
}