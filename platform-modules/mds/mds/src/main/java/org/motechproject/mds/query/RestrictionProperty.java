package org.motechproject.mds.query;

import org.motechproject.mds.util.InstanceSecurityRestriction;

/**
 * The <code>RestrictionProperty</code> class represents a property that will be used in JDO query
 * and depends on restriction criteria the {@code creator} or {@code owner} field in an instance
 * has to have the appropriate user name.
 */
public class RestrictionProperty extends EqualProperty<String> {

    public RestrictionProperty(InstanceSecurityRestriction restriction, String value) {
        super(restriction.isByCreator() ? "creator" : restriction.isByOwner() ? "owner" : "", value,
                String.class.getName());
    }

    @Override
    protected boolean shouldIgnoreThisProperty() {
        return getValue() == null;
    }
}
