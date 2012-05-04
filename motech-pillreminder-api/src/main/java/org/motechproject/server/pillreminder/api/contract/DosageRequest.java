package org.motechproject.server.pillreminder.api.contract;

import java.util.List;

/**
 * \ingroup pillreminder
 * Dosage Request represents Dosage schedule and medicine list for the schedule.
 * startHour and startMinute represent dosage timings.
 * @see MedicineRequest
 */
public class DosageRequest {
    private int startHour;
    private int startMinute;
    private List<MedicineRequest> medicineRequests;

    public DosageRequest(int startHour, int startMinute, List<MedicineRequest> medicineRequests) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.medicineRequests = medicineRequests;
    }

    public List<MedicineRequest> getMedicineRequests() {
        return medicineRequests;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getStartHour() {
        return startHour;
    }
}
