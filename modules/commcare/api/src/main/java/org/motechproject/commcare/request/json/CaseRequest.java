package org.motechproject.commcare.request.json;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CaseRequest {
    private String userId;
    private String caseId;
    private String type;
    private int limit;
    private int offset;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String toQueryString() {
        List<String> queryParams = new ArrayList<>();
        if (userId != null) {
            queryParams.add(concat("user_id", userId));
        }
        if (caseId != null) {
            queryParams.add(concat("case_id", caseId));
        }
        if (type != null) {
            queryParams.add(concat("type", type));
        }
        queryParams.add(concat("limit", limit));
        queryParams.add(concat("offset", offset));

        return StringUtils.join(queryParams, "&");
    }

    private String concat(String key, Object value) {
        return String.format("%s=%s", key, value.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CaseRequest that = (CaseRequest) o;

        return toQueryString().equals(that.toQueryString());
    }

    @Override
    public int hashCode() {
        return toQueryString().hashCode();
    }
}
