package org.motechproject.cmslite.api.web;

import java.io.Serializable;
import java.util.Comparator;

public class ResourceComparator implements Comparator<ResourceDto>, Serializable {
    private static final long serialVersionUID = -3442591167945003657L;

    private final String field;
    private final boolean descending;

    public ResourceComparator(GridSettings settings) {
        this.field = settings.getSortColumn();
        this.descending = settings.isDescending();
    }

    @Override
    public int compare(ResourceDto first, ResourceDto second) {
        int compare;

        switch (field) {
            case "name":
                compare = first.getName().compareToIgnoreCase(second.getName());
                break;
            case "type":
                compare = first.getType().compareToIgnoreCase(second.getType());
                break;
            default:
                compare = 0;
        }

        return compare * (descending ? -1 : 1);
    }
}
