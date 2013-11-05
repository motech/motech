package org.motechproject.mds.web.comparator;

import org.motechproject.mds.dto.EntityDto;

import java.io.Serializable;
import java.util.Comparator;

/**
 * The <code>EntityNameComparator</code> compares two objects of {@link EntityDto} type by their
 * name (it ignores case differences in names).
 */
public class EntityNameComparator implements Comparator<EntityDto>, Serializable {
    private static final long serialVersionUID = 1822927698710354307L;

    /** {@inheritDoc} */
    @Override
    public int compare(EntityDto one, EntityDto two) {
        return one.getName().compareToIgnoreCase(two.getName());
    }

}
