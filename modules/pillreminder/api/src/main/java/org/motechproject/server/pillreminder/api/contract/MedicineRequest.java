package org.motechproject.server.pillreminder.api.contract;

import org.joda.time.LocalDate;

/**
 * \ingroup pillreminder
 * MedicineRequest specifies medicine name and prescription effective date range.
 */
public class MedicineRequest {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    public MedicineRequest(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
