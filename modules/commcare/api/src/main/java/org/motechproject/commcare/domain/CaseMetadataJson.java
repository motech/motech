package org.motechproject.commcare.domain;

import com.google.gson.annotations.SerializedName;

public class CaseMetadataJson {

    @SerializedName("limit")
    private int limit;

    @SerializedName("next")
    private String nextPageQueryString;

    @SerializedName("offset")
    private int offset;

    @SerializedName("previous")
    private String previousPageQueryString;

    @SerializedName("total_count")
    private int totalCount;


    public int getLimit() {
        return limit;
    }

    public String getNextPageQueryString() {
        return nextPageQueryString;
    }

    public int getOffset() {
        return offset;
    }

    public String getPreviousPageQueryString() {
        return previousPageQueryString;
    }

    public int getTotalCount() {
        return totalCount;
    }
}
