package org.motechproject.commcare.service;

import java.util.List;

import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.domain.CaseTask;
import org.motechproject.commcare.response.OpenRosaResponse;
/**
 * This service provides two main features: Interacting with CommCareHQ's programmatic case APIs and uploading case XML wrapped in a form instance to CommCareHQ.
 *
 */
public interface CommcareCaseService {

    /**
     * Query CommCareHQ for a case by its case id and user id.
     * @param caseId The id of the case on CommCareHQ
     * @param userId The user id from CommCareHQ
     * @return A CaseInfo object representing the state of the case or null if that case does not exist.
     */
    CaseInfo getCaseByCaseIdAndUserId(String caseId, String userId);

    /**
     * Query CommCareHQ for a case by its case id.
     * @param caseId The id of the case on CommCareHQ
     * @return A CaseInfo object representing the state of the case or null if that case does not exist.
     */
    CaseInfo getCaseByCaseId(String caseId);

    /**
     * Query CommCareHQ for all cases.
     * @return A list of CaseInfo objects representing all cases found on the configured domain of CommCareHQ
     */
    List<CaseInfo> getAllCases();

    /**
     * Query CommCareHQ for all cases of a given case type.
     * @param type The type of case on CommCareHQ
     * @return A list of CaseInfo objects representing all cases of the given type found on the configured domain of CommCareHQ
     */
    List<CaseInfo> getAllCasesByType(String type);

    /**
     * Query CommCareHQ for all cases under a given user id.
     * @param userId The user id from CommCareHQ
     * @return A list of CaseInfo objects representing all cases under the given user id found on the configured domain of CommCareHQ
     */
    List<CaseInfo> getAllCasesByUserId(String userId);

    /**
     * Query CommCareHQ for all cases of a given case type under a given user id.
     * @param userId The user id from CommCareHQ
     * @param type The type of case on CommCareHQ
     * @return A list of CaseInfo objects representing all cases of the given type under the given user id found on the configured domain of CommCareHQ
     */
    List<CaseInfo> getAllCasesByUserIdAndType(String userId, String type);

    /**
     * Upload case xml wrapped in a minimal xform instance to CommCareHQ.
     * @param caseTask An object representing the case information and case actions to be submitted as case xml
     * @return An informational object representing the status, nature and message of the response from CommCareHQ when attempting to upload this instance of case xml. Returns null if your case xml was incorrect.
     */
    OpenRosaResponse uploadCase(CaseTask caseTask);
}
