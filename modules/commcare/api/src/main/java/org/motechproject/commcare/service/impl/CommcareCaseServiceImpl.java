package org.motechproject.commcare.service.impl;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.httpclient.NameValuePair;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.domain.CaseResponseJson;
import org.motechproject.commcare.domain.CaseTask;
import org.motechproject.commcare.exception.CaseParserException;
import org.motechproject.commcare.gateway.CaseTaskXmlConverter;
import org.motechproject.commcare.response.OpenRosaResponse;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.commcare.util.CommCareAPIHttpClient;
import org.motechproject.dao.MotechJsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class CommcareCaseServiceImpl implements CommcareCaseService {

    @Autowired
    private CaseTaskXmlConverter converter;

    private MotechJsonReader motechJsonReader;

    private CommCareAPIHttpClient commcareHttpClient;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CommcareCaseServiceImpl(CommCareAPIHttpClient commcareHttpClient) {
        this.commcareHttpClient = commcareHttpClient;
        this.motechJsonReader = new MotechJsonReader();
    }

    @Override
    public CaseInfo getCaseByCaseIdAndUserId(String caseId, String userId) {
        NameValuePair[] queryParams = new NameValuePair[2];
        queryParams[0] = new NameValuePair("user_id", userId);
        queryParams[1] = new NameValuePair("case_id", caseId);
        String response = commcareHttpClient.casesRequest(queryParams);
        List<CaseResponseJson> caseResponses = parseCasesFromResponse(response);
        List<CaseInfo> cases = generateCasesFromCaseResponse(caseResponses);
        if (cases.size() == 0) {
            return null;
        }
        return cases.get(0);
    }

    @Override
    public CaseInfo getCaseByCaseId(String caseId) {
        NameValuePair[] queryParams = new NameValuePair[1];
        queryParams[0] = new NameValuePair("case_id", caseId);
        String response = commcareHttpClient.casesRequest(queryParams);
        List<CaseResponseJson> caseResponses = parseCasesFromResponse(response);
        List<CaseInfo> cases = generateCasesFromCaseResponse(caseResponses);
        if (cases.size() == 0) {
            return null;
        }
        return cases.get(0);
    }

    @Override
    public List<CaseInfo> getAllCases() {
        String response = commcareHttpClient.casesRequest(null);
        List<CaseResponseJson> caseResponses = parseCasesFromResponse(response);
        List<CaseInfo> cases = generateCasesFromCaseResponse(caseResponses);
        return cases;
    }

    @Override
    public List<CaseInfo> getAllCasesByType(String type) {
        NameValuePair[] queryParams = new NameValuePair[1];
        queryParams[0] = new NameValuePair("properties/case_type", type);
        String response = commcareHttpClient.casesRequest(queryParams);
        List<CaseResponseJson> caseResponses = parseCasesFromResponse(response);
        List<CaseInfo> cases = generateCasesFromCaseResponse(caseResponses);
        return cases;
    }

    @Override
    public List<CaseInfo> getAllCasesByUserId(String userId) {
        NameValuePair[] queryParams = new NameValuePair[1];
        queryParams[0] = new NameValuePair("user_id", userId);
        String response = commcareHttpClient.casesRequest(queryParams);
        List<CaseResponseJson> caseResponses = parseCasesFromResponse(response);
        List<CaseInfo> cases = generateCasesFromCaseResponse(caseResponses);
        return cases;
    }

    @Override
    public List<CaseInfo> getAllCasesByUserIdAndType(String userId, String type) {
        NameValuePair[] queryParams = new NameValuePair[2];
        queryParams[0] = new NameValuePair("user_id", type);
        queryParams[1] = new NameValuePair("properties/case_type=", type);
        String response = commcareHttpClient.casesRequest(queryParams);
        List<CaseResponseJson> caseResponses = parseCasesFromResponse(response);
        List<CaseInfo> cases = generateCasesFromCaseResponse(caseResponses);
        return cases;
    }

    private List<CaseResponseJson> parseCasesFromResponse(String response) {
        Type commcareCaseType = new TypeToken<List<CaseResponseJson>>() {
        } .getType();

        List<CaseResponseJson> allCases = new ArrayList<CaseResponseJson>();

        try {
            allCases = (List<CaseResponseJson>) motechJsonReader
                    .readFromString(response, commcareCaseType);
        } catch (Exception e) {
            logger.warn("Exception while trying to read in case JSON: " + e.getMessage());
        }

        return allCases;
    }

    private List<CaseInfo> generateCasesFromCaseResponse(
            List<CaseResponseJson> caseResponses) {
        List<CaseInfo> caseList = new ArrayList<CaseInfo>();

        if (caseResponses == null) {
            return Collections.emptyList();
        }

        for (CaseResponseJson caseResponse : caseResponses) {
            CaseInfo caseInfo = new CaseInfo();

            Map<String, String> properties = caseResponse.getProperties();

            String caseType = properties.get("case_type");
            String dateOpened = properties.get("date_opened");
            String ownerId = properties.get("owner_id)");
            String caseName = properties.get("case_name");

            caseInfo.setCaseType(caseType);
            caseInfo.setDateOpened(dateOpened);
            caseInfo.setOwnerId(ownerId);
            caseInfo.setCaseName(caseName);

            properties.remove("case_type");
            properties.remove("date_opened");
            properties.remove("owner_id");
            properties.remove("case_name");

            caseInfo.setFieldValues(properties);
            caseInfo.setClosed(caseResponse.isClosed());
            caseInfo.setDateClosed(caseResponse.getDateClosed());
            caseInfo.setDomain(caseResponse.getDomain());
            caseInfo.setIndices(caseResponse.getIndices());
            caseInfo.setServerDateModified(caseResponse.getServerDateModified());
            caseInfo.setServerDateOpened(caseResponse.getServerDateOpened());
            caseInfo.setVersion(caseResponse.getVersion());
            caseInfo.setXformIds(caseResponse.getXformIds());
            caseInfo.setCaseId(caseResponse.getCaseId());
            caseInfo.setUserId(caseResponse.getUserId());

            caseList.add(caseInfo);

        }

        return caseList;
    }

    @Override
    public OpenRosaResponse uploadCase(CaseTask caseTask) {

        String caseXml = converter.convertToCaseXml(caseTask);
        String fullXml = "<?xml version='1.0'?>\n" + caseXml;

        OpenRosaResponse response;

        try {
            response = commcareHttpClient.caseUploadRequest(fullXml);
        } catch (CaseParserException e) {
            return null;
        }

        return response;
    }
}
