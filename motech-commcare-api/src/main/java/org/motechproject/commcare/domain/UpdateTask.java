package org.motechproject.commcare.domain;

/**
 * Class that represents an update element in
 * case XML. Inclusion of this object into
 * the CaseTask will generate an update element
 * in the XML.
 *
 */
import java.util.Map;

public class UpdateTask {

    private String caseType;
    private String caseName;;
    private String dateOpened;
    private String ownerId;

    private Map<String, String> fieldValues;

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(String dateOpened) {
        this.dateOpened = dateOpened;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Map<String, String> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(Map<String, String> fieldValues) {
        this.fieldValues = fieldValues;
    }



}
