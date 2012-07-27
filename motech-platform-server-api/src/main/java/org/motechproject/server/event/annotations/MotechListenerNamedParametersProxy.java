package org.motechproject.server.event.annotations;

import org.motechproject.scheduler.domain.MotechEvent;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yyonkov
 */
public class MotechListenerNamedParametersProxy extends MotechListenerAbstractProxy {

    /**
     * @param name
     * @param bean
     * @param method
     */
    public MotechListenerNamedParametersProxy(String name, Object bean, Method method) {
        super(name, bean, method);
    }

    /* (non-Javadoc)
      * @see org.motechproject.server.event.annotations.MotechListenerAbstractProxy#callHandler(org.motechproject.scheduler.model.MotechEvent)
      */
    @Override
    public void callHandler(MotechEvent event) {
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
            Assert.notNull(arg, String.format("parameter #%d with name:\"%s\" not found or null prameter passed.", i, annotation.value()));
            Assert.isAssignable(t, arg.getClass(), String.format("Parameter #%d expected subtypes of %s passed %s.", i, t.getName(), arg.getClass().getName()));
            args.add(arg);
        }
        ReflectionUtils.invokeMethod(getMethod(), getBean(), args.toArray());

    }
}
