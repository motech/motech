package org.motechproject.server.pillreminder.domain;

import java.util.Set;
import java.util.UUID;

public class Dosage{
    private String id;
    private Integer startHour;
    private Integer startMinute;
    private Set<Medicine> medicines;
    private boolean reminded = false;

    public Dosage() {
    }

    public Dosage(Integer startHour, Integer startMinute, Set<Medicine> medicines) {
        this.id = UUID.randomUUID().toString();
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.medicines = medicines;
    }

    public String getId() {
        return id;
    }

    public Set<Medicine> getMedicines() {
        return medicines;
    }

    public void setMedicines(Set<Medicine> medicines) {
        this.medicines = medicines;
    }

    public Integer getStartHour() {
        return startHour;
    }

    public void setStartHour(Integer startHour) {
        this.startHour = startHour;
    }

    public Integer getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(Integer startMinute) {
        this.startMinute = startMinute;
    }

    public boolean isReminded() {
        return reminded;
    }

    public void setReminded(boolean reminded) {
        this.reminded = reminded;
    }
}