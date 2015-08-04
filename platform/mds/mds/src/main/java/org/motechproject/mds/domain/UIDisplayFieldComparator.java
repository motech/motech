
package org.motechproject.mds.domain;

import java.util.Comparator;

/**
 * <code>UIDisplayFieldComparator</code> compares positions
 * added in UIDisplayable annotation. Fields without annotation are placed at the end.
 */
public class UIDisplayFieldComparator implements Comparator<Field> {

    @Override
    public int compare(Field o1, Field o2) {
        // check if one is displayable and the other isn't
        if (o1.isUIDisplayable() && !o2.isUIDisplayable()) {
            return -1;
        } else if (!o1.isUIDisplayable() && o2.isUIDisplayable()) {
            return 1;
        }

        // compare positions
        Long position1 = o1.getUIDisplayPosition();
        Long position2 = o2.getUIDisplayPosition();

        if (position1 == null) {
            return 1;
        } else if (position2 == null) {
            return -1;
        } else {
            return (position1 > position2) ? 1 : -1;
        }
    }
}
