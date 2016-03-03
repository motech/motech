package org.motechproject.mds.query;

import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertNull;

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
    protected Property getRelatedProperty() {
        return new SetProperty<>("jdoVar", "set.id", SET, Integer.class.getName());
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
    protected String expectedFilterForRelatedField() {
        return "(set.contains(jdoVar) && (jdoVar.id == param0_0 || jdoVar.id == param0_1 || jdoVar.id == param0_2))";
    }

    @Override
    protected String expectedDeclareParameter() {
        return "java.lang.Integer param0_0, java.lang.Integer param0_1, java.lang.Integer param0_2";
    }

    @Override
    protected Collection expectedUnwrap() {
        return SET;
    }

    @Test
    public void shouldIgnoreEmptySet() {
        SetProperty<String> property = new SetProperty<>("name", Collections.<String>emptySet(), String.class.getName());
        assertNull(property.asFilter(0));
        assertNull(property.asDeclareParameter(0));
    }
}
