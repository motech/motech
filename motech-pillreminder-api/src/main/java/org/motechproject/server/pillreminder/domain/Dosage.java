package org.motechproject.server.pillreminder.domain;

import java.util.*;

public class Dosage{
    private String id;
    private int startHour;
    private int startMinute;
    private Set<Medicine> medicines;

    public Dosage() {
    }

    public Dosage(int startHour, int startMinute, Set<Medicine> medicines) {
        this.id = UUID.randomUUID().toString();
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.medicines = medicines;
    }

    public Set<Medicine> getMedicines() {
        return medicines;
    }

    public void setMedicines(Set<Medicine> medicines) {
        this.medicines = medicines;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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