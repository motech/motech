package org.motechproject.commcare.request;

import java.util.ArrayList;
import java.util.List;

public class IndexElement {

    private List<IndexSubElement> subElements = new ArrayList<IndexSubElement>();


    public IndexElement(List<IndexSubElement> subElements) {
        this.subElements = subElements;
    }

    public List<IndexSubElement> getSubElements() {
        return subElements;
    }
}
