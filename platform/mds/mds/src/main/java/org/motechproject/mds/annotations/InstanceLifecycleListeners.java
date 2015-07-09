package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>InstanceLifecycleListeners</code> annotation is used to point entities for which
 * there may exist instance lifecycle listeners. The only valid case for using this option is
 * when listeners are registered programmatically using the
 * {@link org.motechproject.mds.service.JdoListenerRegistryService#registerListener(org.motechproject.mds.listener.MotechLifecycleListener)}
 * service method. There is no need to bother with this annotation when listeners are registered with
 * {@link InstanceLifecycleListener}.
 *
 * @see org.motechproject.mds.annotations.internal.InstanceLifecycleListenersProcessor
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InstanceLifecycleListeners {
}
