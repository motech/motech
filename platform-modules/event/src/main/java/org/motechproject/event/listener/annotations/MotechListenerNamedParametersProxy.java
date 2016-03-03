package org.motechproject.event.listener.annotations;

import org.motechproject.event.MotechEvent;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the type of <code>MotechListener</code> proxy where handler is a method with
 * parameters defined by the {@link org.motechproject.event.listener.annotations.MotechParam}
 * annotation.
 *
 * @author yyonkov
 */
public class MotechListenerNamedParametersProxy extends MotechListenerAbstractProxy {

    /**
      * @see org.motechproject.event.listener.annotations.MotechListenerAbstractProxy#MotechListenerAbstractProxy(String, Object, java.lang.reflect.Method)
      */
    public MotechListenerNamedParametersProxy(String name, Object bean, Method method) {
        super(name, bean, method);
    }

    @Override
    public void callHandler(MotechEvent event) {
        ReflectionUtils.invokeMethod(getMethod(), getBean(), getParameters(event).toArray());
    }

    private List<Object> getParameters(MotechEvent event) {
        List<Object> args = new ArrayList<Object>();
        Class<?>[] paramTypes = getMethod().getParameterTypes();
        Annotation[][] paramAnnotations = getMethod().getParameterAnnotations();
        Assert.isTrue(paramTypes.length == paramAnnotations.length);
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> t = paramTypes[i];
            Assert.notEmpty(paramAnnotations[i], "MotechParam(name) annotation is required for each parameter.");
            //TODO now assuming only MotechParam annotation is present...
            Assert.isAssignable(MotechParam.class, paramAnnotations[i][0].getClass());
            MotechParam annotation = (MotechParam) paramAnnotations[i][0];
            Object arg = event.getParameters().get(annotation.value());
            Assert.notNull(arg, String.format("parameter #%d with name:\"%s\" not found or null parameter passed.", i, annotation.value()));
            Assert.isAssignable(t, arg.getClass(), String.format("Parameter #%d expected subtypes of %s passed %s.", i, t.getName(), arg.getClass().getName()));
            args.add(arg);
        }
        return args;
    }
}
