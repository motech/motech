package org.motechproject.tasks.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.ParameterType;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MethodHandlerTest {
    private static final String STRING_VALUE = "abc";
    private static final int INTEGER_VALUE = 1;
    private static final double DOUBLE_VALUE = 0.5;
    private static final DateTime DATETIME_VALUE = new DateTime(2013, 3, 6, 10, 0, 0, 0);

    @Test
    public void shouldNotBeParametrized() {
        assertFalse(new MethodHandler(null, null).isParametrized());
        assertFalse(new MethodHandler(new ActionEvent(), null).isParametrized());
        assertFalse(new MethodHandler(new ActionEvent(null, null, null, new TreeSet<ActionParameter>()), null).isParametrized());
    }

    @Test
    public void shouldBeParametrized() {
        ActionEvent action = new ActionEvent();
        action.addParameter(new ActionParameter("String", "string"), true);
        action.addParameter(new ActionParameter("Integer", "integer", ParameterType.INTEGER), true);
        action.addParameter(new ActionParameter("Double", "double", ParameterType.DOUBLE), true);
        action.addParameter(new ActionParameter("Date", "date", ParameterType.DATE), true);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("string", STRING_VALUE);
        parameters.put("integer", INTEGER_VALUE);
        parameters.put("double", DOUBLE_VALUE);
        parameters.put("date", DATETIME_VALUE);

        MethodHandler methodHandler = new MethodHandler(action, parameters);

        assertTrue(methodHandler.isParametrized());
        assertArrayEquals(new Class[]{String.class, Integer.class, Double.class, DateTime.class}, methodHandler.getClasses());
        assertArrayEquals(new Object[]{STRING_VALUE, INTEGER_VALUE, DOUBLE_VALUE, DATETIME_VALUE}, methodHandler.getObjects());
    }
}
