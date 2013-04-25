package org.motechproject.tasks.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.ParameterType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MethodHandlerTest {
    private static final ArrayList<Object> LIST_VALUE = new ArrayList<>();
    private static final DateTime DATETIME_VALUE = new DateTime(2013, 3, 6, 10, 0, 0, 0);
    private static final String STRING_VALUE = "abc";
    private static final TreeMap<Object, Object> MAP_VALUE = new TreeMap<>();
    private static final boolean BOOLEAN_VALUE = true;
    private static final double DOUBLE_VALUE = 0.5;
    private static final int INTEGER_VALUE = 1;
    private static final long LONG_VALUE = 10000000000L;

    @Test
    public void shouldNotBeParametrized() {
        assertFalse(new MethodHandler(null, null).isParametrized());
        assertFalse(new MethodHandler(new ActionEvent(null, null, null, new TreeSet<ActionParameter>()), null).isParametrized());
    }

    @Test
    public void shouldBeParametrized() {
        ActionEvent action = new ActionEvent();
        action.addParameter(new ActionParameter("String", "string"), true);
        action.addParameter(new ActionParameter("Integer", "integer", ParameterType.INTEGER), true);
        action.addParameter(new ActionParameter("Long", "long", ParameterType.LONG), true);
        action.addParameter(new ActionParameter("Double", "double", ParameterType.DOUBLE), true);
        action.addParameter(new ActionParameter("Boolean", "boolean", ParameterType.BOOLEAN), true);
        action.addParameter(new ActionParameter("Date", "date", ParameterType.DATE), true);
        action.addParameter(new ActionParameter("Map", "map", ParameterType.MAP), true);
        action.addParameter(new ActionParameter("List", "list", ParameterType.LIST), true);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("string", STRING_VALUE);
        parameters.put("integer", INTEGER_VALUE);
        parameters.put("long", LONG_VALUE);
        parameters.put("double", DOUBLE_VALUE);
        parameters.put("boolean", BOOLEAN_VALUE);
        parameters.put("date", DATETIME_VALUE);
        parameters.put("map", MAP_VALUE);
        parameters.put("list", LIST_VALUE);

        Class[] expectedClassArray = {String.class, Integer.class, Long.class, Double.class, Boolean.class, DateTime.class, Map.class, List.class};
        Object[] expectedObjectArray = {STRING_VALUE, INTEGER_VALUE, LONG_VALUE, DOUBLE_VALUE, BOOLEAN_VALUE, DATETIME_VALUE, MAP_VALUE, LIST_VALUE};

        MethodHandler methodHandler = new MethodHandler(action, parameters);

        assertTrue(methodHandler.isParametrized());
        assertArrayEquals(expectedClassArray, methodHandler.getClasses());
        assertArrayEquals(expectedObjectArray, methodHandler.getObjects());
    }
}
