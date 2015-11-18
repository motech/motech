package org.motechproject.mds.dto;

import java.util.Comparator;
import java.util.List;

/**
 * <code>UIDisplayFieldComparator</code> compares positions
 * added in UIDisplayable annotation. Fields without annotation are placed at the end.
 */
public class UIDisplayFieldComparator implements Comparator<FieldDto>{

    private static final int NOT_DISPLAYABLE = -1;

    private final List<Number> displayableFieldIds;

    public UIDisplayFieldComparator(List<Number> displayableFieldIds) {
        this.displayableFieldIds = displayableFieldIds;
    }

    @Override
    public int compare(FieldDto o1, FieldDto o2) {
        // check if one is displayable and the other isn't
        if (isUIDisplayable(o1) && !isUIDisplayable(o2)) {
            return -1;
        } else if (!isUIDisplayable(o1) && isUIDisplayable(o2)) {
            return 1;
        }

        // compare positions
        int position1 = getUIDisplayPosition(o1);
        int position2 = getUIDisplayPosition(o2);

        if (position1 == NOT_DISPLAYABLE) {
            return 1;
        } else if (position2 == NOT_DISPLAYABLE) {
            return -1;
        } else {
            return (position1 > position2) ? 1 : -1;
        }
    }

    private boolean isUIDisplayable(FieldDto fieldDto) {
        return displayableFieldIds.contains(fieldDto.getId());
    }

    private int getUIDisplayPosition(FieldDto fieldDto) {
        return displayableFieldIds.indexOf(fieldDto.getId());
    }
}
