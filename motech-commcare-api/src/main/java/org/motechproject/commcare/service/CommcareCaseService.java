package org.motechproject.commcare.service;

import java.util.List;

import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.domain.CaseTask;
import org.motechproject.commcare.response.OpenRosaResponse;

public interface CommcareCaseService {

    public CaseInfo getCaseByCaseIdAndUserId(String caseId, String userId);

    public CaseInfo getCaseByCaseId(String caseId);

    public List<CaseInfo> getAllCases();

    public List<CaseInfo> getAllCasesByType(String type);

    public List<CaseInfo> getAllCasesByUserId(String userId);

    public List<CaseInfo> getAllCasesByUserIdAndType(String userId, String type);

    public OpenRosaResponse uploadCase(CaseTask caseTask);
}
