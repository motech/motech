package org.motechproject.server.pillreminder.contract;

import org.motechproject.model.Time;

import java.util.Date;
import java.util.List;

public class DosageResponse {
    private String dosageId;
    private int dosageHour;
    private int dosageMinute;
    private Date startDate;
    private Date endDate;
    private Date lastTakenDate;
    private List<MedicineResponse> medicines;

    public DosageResponse(String dosageId, Time dosageTime, Date startDate, Date endDate, Date lastTakenDate, List<MedicineResponse> medicines) {
        this.dosageId = dosageId;
        this.dosageHour = dosageTime.getHour();
        this.dosageMinute = dosageTime.getMinute();
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastTakenDate = lastTakenDate;
        this.medicines = medicines;
    }

    public String getDosageId() {
        return dosageId;
    }

    public int getDosageHour() {
        return dosageHour;
    }

    public int getDosageMinute() {
        return dosageMinute;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getLastTakenDate() {
        return lastTakenDate;
    }

    public List<MedicineResponse> getMedicines() {
        return medicines;
    }
}
