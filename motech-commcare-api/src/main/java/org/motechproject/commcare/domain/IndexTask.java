package org.motechproject.commcare.domain;

/**
 * Class that represents the index element
 * of case XML. Including this class in the CaseTask
 * object will generate an index element in the case xml,
 * with all of the provided indices.
 */
import java.util.List;

import org.motechproject.commcare.request.IndexSubElement;

public class IndexTask {

    private List<IndexSubElement> indices;

    public IndexTask(List<IndexSubElement> indices) {
        this.indices = indices;
    }

    public List<IndexSubElement> getIndices() {
        return indices;
    }

    public void setIndices(List<IndexSubElement> indices) {
        this.indices = indices;
    }

}
