package org.motechproject.server.pillreminder.contract;

import java.util.List;

public class DosageRequest {
    private Integer startHour;
    private Integer startMinute;
    private List<String> medicineRequests;

    public DosageRequest(Integer startHour, Integer startMinute, List<String> medicineRequests) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.medicineRequests = medicineRequests;
    }

    public List<String> getMedicineRequests() {
        return medicineRequests;
    }

    public Integer getStartMinute() {
        return startMinute;
    }

    public Integer getStartHour() {
        return startHour;
    }
}
