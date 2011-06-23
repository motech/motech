package com.motechproject.server.pillreminder.contract;

import java.util.Date;
import java.util.List;

public class PillRegimenRequest {
    private String externalId;
    private Date startDate;
    private Date endDate;
    private List<DosageRequest> dosageContracts;

    public PillRegimenRequest(String externalId, Date startDate, Date endDate, List<DosageRequest> dosageContracts) {
        this.externalId = externalId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dosageContracts = dosageContracts;
    }

    public String getExternalId() {
        return externalId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public List<DosageRequest> getDosageContracts() {
        return dosageContracts;
    }
}
