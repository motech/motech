package org.motechproject.messagecampaign.service;

import org.joda.time.LocalDate;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollmentStatus;

/**
 * \defgroup MessageCampaign Message Campaign
 */
/**
 * \ingroup MessageCampaign
 *
 * This is the record which will be returned when message campaign service is queried for enrollments
 * It holds the details of an enrollment
 *
 */
public class CampaignEnrollmentRecord {
    private String externalId;
    private String campaignName;
    private LocalDate referenceDate;
    private CampaignEnrollmentStatus status;

    /**
     * This is the constructor which is used to create an CampaignEnrollmentRecord
     * @param externalId
     * @param campaignName
     * @param referenceDate
     * @param status
     */
    public CampaignEnrollmentRecord(String externalId, String campaignName, LocalDate referenceDate, CampaignEnrollmentStatus status) {
        this.externalId = externalId;
        this.campaignName = campaignName;
        this.referenceDate = referenceDate;
        this.status = status;
    }

    /**
     * This returns the External Id of a CampaignEnrollmentRecord
     * @return String
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * This returns the Campaign Name of a CampaignEnrollmentRecord
     * @return String
     */
    public String getCampaignName() {
        return campaignName;
    }

    /**
     * This returns the Start Date of a CampaignEnrollmentRecord
     * @return LocalDate
     */
    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    /**
     * This returns the Status of a CampaignEnrollmentRecord
     * @return CampaignEnrollmentStatus
     */
    public CampaignEnrollmentStatus getStatus() {
        return status;
    }
}
