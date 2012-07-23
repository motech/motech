package org.motechproject.server.event.annotations;

import org.motechproject.scheduler.domain.MotechEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Dispatches ordered parameters MotechEvent {"0":Obj0, "1":Obj1, .... "n":ObjN} to appropriate method signature at runtime
 * NOTE: It might be better append the method signature to the end of the subjects
 *
 * @author yyonkov
 */
public class MotechListenerOrderedParametersProxy extends MotechListenerAbstractProxy {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public MotechListenerOrderedParametersProxy(String name, Object bean, Method method) {
        super(name, bean, method);
    }

    /* (non-Javadoc)
      * @see org.motechproject.server.event.annotations.MotechListenerAbstractProxy#callHandler(org.motechproject.scheduler.model.MotechEvent)
      */
    @Override
    public void callHandler(MotechEvent event) {
        Map<String, Object> params = event.getParameters();
        List<Object> args = new ArrayList<Object>();
        int i = 0;
        for (Class<?> t : getMethod().getParameterTypes()) {
            Object param = params.get(Integer.toString(i));
            if (param != null && t.isAssignableFrom(param.getClass())) {
                args.add(param);
                i++;
            } else if (params.containsKey(Integer.toString(i)) && !t.isPrimitive() && param == null) {
                args.add(param);
                i++;
            } else {
                logger.warn(String.format("Method: %s parameter: #%d of type: %s is not available in the event: %s. Handler skiped...", getMethod().toGenericString(), i, t.getName(), event));
                return;
            }
        }
        ReflectionUtils.invokeMethod(getMethod(), getBean(), args.toArray());
    }
}
