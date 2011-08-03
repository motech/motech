package org.motechproject.server.pillreminder.contract;

import java.util.List;

public class DosageResponse {
    private int startHour;
    private int startMinute;
    private String dosageId;
    private List<String> medicines;

    public DosageResponse(int startHour, int startMinute, String dosageId, List<String> medicines) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.dosageId = dosageId;
        this.medicines = medicines;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public String getDosageId() {
        return dosageId;
    }

    public List<String> getMedicines() {
        return medicines;
    }
}
