package org.motechproject.mds.annotations;

import org.motechproject.mds.domain.InstanceLifecycleListenerType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>InstanceLifecycleListener</code> annotation is used to point methods
 * from the services exposed by OSGi that should listen to persistence events. The InstanceLifecycleListenerType
 * value is an array of one or more values specified in {@link org.motechproject.mds.domain.InstanceLifecycleListenerType}
 * enum, that is: POST_CREATE, PRE_DELETE, POST_DELETE, POST_LOAD, PRE_STORE, POST_STORE.
 * The annotated methods must have only one parameter with type being a persistable class.
 * <p/>
 * This annotation is processed by
 * {@link org.motechproject.mds.annotations.internal.InstanceLifecycleListenerProcessor}.
 *
 * @see org.motechproject.mds.annotations.internal.InstanceLifecycleListenerProcessor
 * @see org.motechproject.mds.domain.InstanceLifecycleListenerType
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InstanceLifecycleListener {
    InstanceLifecycleListenerType[] value();
}
