package org.motechproject.server.pillreminder.api.contract;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;

import java.util.List;

/**
 * \ingroup pillreminder
 * Dosage details returned from pill reminder module, represents single dosage detail (morning or evening)
 * Also contains information about last dosage confirmation.
 */
public class DosageResponse {
    private String dosageId;
    private int dosageHour;
    private int dosageMinute;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate responseLastCapturedDate;
    private List<MedicineResponse> medicines;

    public DosageResponse(String dosageId, Time dosageTime, LocalDate startDate, LocalDate endDate, LocalDate responseLastCapturedDate, List<MedicineResponse> medicines) {
        this.dosageId = dosageId;
        this.dosageHour = dosageTime.getHour();
        this.dosageMinute = dosageTime.getMinute();
        this.startDate = startDate;
        this.endDate = endDate;
        this.responseLastCapturedDate = responseLastCapturedDate;
        this.medicines = medicines;
    }

    /** Unique dosage identifier */
    public String getDosageId() {
        return dosageId;
    }

    /** Dosage time -- hour component */
    public int getDosageHour() {
        return dosageHour;
    }

    /** Dosage time -- minute component */
    public int getDosageMinute() {
        return dosageMinute;
    }

    /** Dosage start date, if there are multiple medicines with different start date then first start date will be returned */
    public LocalDate getStartDate() {
        return startDate;
    }

    /** Dosage end date, if there are multiple medicines with different end date then last ending will be returned */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * @return Last dosage confirmation from subscriber for this dose.
     */
    public LocalDate getResponseLastCapturedDate() {
        return responseLastCapturedDate;
    }

    /**
     * @return Medicines prescribed
     */
    public List<MedicineResponse> getMedicines() {
        return medicines;
    }
}
