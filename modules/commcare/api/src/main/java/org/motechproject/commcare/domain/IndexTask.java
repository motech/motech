package org.motechproject.commcare.domain;

/**
 * A domain class to include in a case task in order to generate an index element with IndexSubElements as the indices.
 */

import org.motechproject.commcare.request.IndexSubElement;

import java.util.ArrayList;
import java.util.List;

public class IndexTask {
    private List<IndexSubElement> indices;

    public IndexTask(List<IndexSubElement> indices) {
        if (indices == null) {
            this.indices = new ArrayList<IndexSubElement>();
        } else {
            this.indices = indices;
        }
    }

    public List<IndexSubElement> getIndices() {
        return this.indices;
    }

    public void setIndices(List<IndexSubElement> indices) {
        this.indices = indices;
    }
}
