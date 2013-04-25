package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

class MethodHandler {
    private boolean parametrized;
    private Class[] classes;
    private Object[] objects;

    public MethodHandler(ActionEvent action, Map<String, Object> parameters) {
        if (action != null) {
            SortedSet<ActionParameter> actionParameters = action.getActionParameters();

            if (!actionParameters.isEmpty()) {
                parametrized = true;
                classes = new Class[parameters.size()];
                objects = new Object[parameters.size()];

                for (ActionParameter actionParameter : actionParameters) {
                    Object obj = parameters.get(actionParameter.getKey());
                    Integer idx = actionParameter.getOrder();

                    objects[idx] = obj;

                    if (obj instanceof Map) {
                        classes[idx] = Map.class;
                    } else if (obj instanceof List) {
                        classes[idx] = List.class;
                    } else {
                        classes[idx] = obj.getClass();
                    }
                }
            }
        }
    }

    public boolean isParametrized() {
        return parametrized;
    }

    public Class[] getClasses() {
        return Arrays.copyOf(classes, classes.length);
    }

    public Object[] getObjects() {
        return Arrays.copyOf(objects, objects.length);
    }
}
