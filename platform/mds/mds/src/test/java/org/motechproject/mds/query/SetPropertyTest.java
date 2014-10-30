package org.motechproject.mds.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SetPropertyTest extends PropertyTest {
    private static final Set<Integer> SET = new HashSet<>();

    static {
        SET.add(1);
        SET.add(2);
        SET.add(3);
    }

    @Override
    protected Property getProperty() {
        return new SetProperty<>("set", SET, Integer.class.getName());
    }

    @Override
    protected int getIdx() {
        return 0;
    }

    @Override
    protected String expectedFilter() {
        return "(set == param0_0 || set == param0_1 || set == param0_2)";
    }

    @Override
    protected String expectedDeclareParameter() {
        return "java.lang.Integer param0_0, java.lang.Integer param0_1, java.lang.Integer param0_2";
    }

    @Override
    protected Collection expectedUnwrap() {
        return SET;
    }
}
