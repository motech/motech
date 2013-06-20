package org.motechproject.commcare.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CaseResponseJson {

    @SerializedName("meta")
    private CaseMetadataJson metadata;

    @SerializedName("objects")
    private List<CaseJson> cases;

    public CaseMetadataJson getMetadata() {
        return metadata;
    }

    public List<CaseJson> getCases() {
        return cases;
    }


}
