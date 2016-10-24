package org.motechproject.tasks.service.impl;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.tasks.domain.mds.channel.ActionEvent;
import org.motechproject.tasks.domain.mds.channel.builder.ActionEventBuilder;
import org.motechproject.tasks.domain.mds.channel.builder.ActionParameterBuilder;
import org.motechproject.tasks.domain.enums.MethodCallManner;
import org.motechproject.tasks.domain.enums.ParameterType;
import org.motechproject.tasks.exception.TaskHandlerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertArrayEquals;

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
    public void shouldConstructMethodHandlerForMapMethodCall() throws TaskHandlerException {
        ActionEvent action = getActionEvent(MethodCallManner.MAP);
        Map<String, Object> parameters = getParameters();

        MethodHandler methodHandler = new MethodHandler(action, parameters);

        Class[] expectedClassArray = {Map.class};
        Object[] expectedObjectArray = {parameters};

        assertArrayEquals(expectedClassArray, methodHandler.getClasses());
        assertArrayEquals(expectedObjectArray, methodHandler.getObjects());
    }

    @Test
    public void shouldConstructMethodHandlerForNamedParametersMethodCall() throws TaskHandlerException {
        ActionEvent action = getActionEvent(MethodCallManner.NAMED_PARAMETERS);
        Map<String, Object> parameters = getParameters();

        Class[] expectedClassArray = {String.class, Integer.class, Long.class, Double.class, Boolean.class, DateTime.class, Map.class, List.class};
        Object[] expectedObjectArray = {STRING_VALUE, INTEGER_VALUE, LONG_VALUE, DOUBLE_VALUE, BOOLEAN_VALUE, DATETIME_VALUE, MAP_VALUE, LIST_VALUE};

        MethodHandler methodHandler = new MethodHandler(action, parameters);

        assertArrayEquals(expectedClassArray, methodHandler.getClasses());
        assertArrayEquals(expectedObjectArray, methodHandler.getObjects());
    }

    @Test
    public void shouldConstructMethodHandlerForNamedParametersMethodCallByDefault() throws TaskHandlerException {
        ActionEvent action = getActionEvent(MethodCallManner.NAMED_PARAMETERS);
        action.setServiceMethodCallManner(null);

        Map<String, Object> parameters = getParameters();

        Class[] expectedClassArray = {String.class, Integer.class, Long.class, Double.class, Boolean.class, DateTime.class, Map.class, List.class};
        Object[] expectedObjectArray = {STRING_VALUE, INTEGER_VALUE, LONG_VALUE, DOUBLE_VALUE, BOOLEAN_VALUE, DATETIME_VALUE, MAP_VALUE, LIST_VALUE};

        MethodHandler methodHandler = new MethodHandler(action, parameters);

        assertArrayEquals(expectedClassArray, methodHandler.getClasses());
        assertArrayEquals(expectedObjectArray, methodHandler.getObjects());
    }

    private ActionEvent getActionEvent(MethodCallManner callManner) {
        ActionEvent action = new ActionEventBuilder().setServiceMethodCallManner(callManner).build();
        action.addParameter(new ActionParameterBuilder().setDisplayName("String").setKey("string").build(), true);
        action.addParameter(new ActionParameterBuilder().setDisplayName("Integer").setKey("integer").setType(ParameterType.INTEGER).build(), true);
        action.addParameter(new ActionParameterBuilder().setDisplayName("Long").setKey("long").setType(ParameterType.LONG).build(), true);
        action.addParameter(new ActionParameterBuilder().setDisplayName("Double").setKey("double").setType(ParameterType.DOUBLE).build(), true);
        action.addParameter(new ActionParameterBuilder().setDisplayName("Boolean").setKey("boolean").setType(ParameterType.BOOLEAN).build(), true);
        action.addParameter(new ActionParameterBuilder().setDisplayName("Date").setKey("date").setType(ParameterType.DATE).build(), true);
        action.addParameter(new ActionParameterBuilder().setDisplayName("Map").setKey("map").setType(ParameterType.MAP).build(), true);
        action.addParameter(new ActionParameterBuilder().setDisplayName("List").setKey("list").setType(ParameterType.LIST).build(), true);
        return action;
    }

    private Map<String, Object> getParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("string", STRING_VALUE);
        parameters.put("integer", INTEGER_VALUE);
        parameters.put("long", LONG_VALUE);
        parameters.put("double", DOUBLE_VALUE);
        parameters.put("boolean", BOOLEAN_VALUE);
        parameters.put("date", DATETIME_VALUE);
        parameters.put("map", MAP_VALUE);
        parameters.put("list", LIST_VALUE);
        return parameters;
    }
}
