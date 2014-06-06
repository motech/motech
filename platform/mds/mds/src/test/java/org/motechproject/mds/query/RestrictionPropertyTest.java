package org.motechproject.mds.query;

import org.motechproject.mds.util.InstanceSecurityRestriction;

import java.util.Arrays;
import java.util.Collection;

public class RestrictionPropertyTest extends PropertyTest {

    @Override
    protected Property getProperty() {
        InstanceSecurityRestriction restriction = new InstanceSecurityRestriction();
        restriction.setByCreator(true);

        return new RestrictionProperty(restriction, "motech");
    }

    @Override
    protected int getIdx() {
        return 8;
    }

    @Override
    protected String expectedFilter() {
        return "creator == param8";
    }

    @Override
    protected String expectedDeclareParameter() {
        return "java.lang.String param8";
    }

    @Override
    protected Collection expectedUnwrap() {
        return Arrays.asList("motech");
    }

}
