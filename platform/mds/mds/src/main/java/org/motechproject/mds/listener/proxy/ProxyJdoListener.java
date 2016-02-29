package org.motechproject.mds.listener.proxy;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.motechproject.mds.annotations.InstanceLifecycleListenerType;
import org.motechproject.mds.exception.JdoListenerInvocationException;
import org.motechproject.mds.listener.MotechLifecycleListener;
import org.motechproject.mds.service.JdoListenerRegistryService;
import org.motechproject.osgi.web.util.OSGiServiceUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import javax.jdo.listener.CreateLifecycleListener;
import javax.jdo.listener.DeleteLifecycleListener;
import javax.jdo.listener.InstanceLifecycleEvent;
import javax.jdo.listener.LoadLifecycleListener;
import javax.jdo.listener.StoreLifecycleListener;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

/**
 * The <code>ProxyJdoListener</code> is a listener for persistence events. This listener is for a set of defined classes
 * which are registered in DataNucleus by {@link org.motechproject.mds.listener.register.JdoListenerRegister}. It is responsible
 * for invoking methods, which are annotated by {@link org.motechproject.mds.annotations.InstanceLifecycleListener}.
 *
 * @see org.motechproject.mds.listener.register.JdoListenerRegister
 * @see org.motechproject.mds.annotations.InstanceLifecycleListener
 */
public class ProxyJdoListener implements CreateLifecycleListener, StoreLifecycleListener,
        DeleteLifecycleListener, LoadLifecycleListener {

    private final BundleContext bundleContext;
    private JdoListenerRegistryService jdoListenerRegistryService;

    public ProxyJdoListener() {
        this.bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
    }

    @Override
    public void postCreate(InstanceLifecycleEvent event) {
        invokeMethods(event.getPersistentInstance(), InstanceLifecycleListenerType.POST_CREATE);
    }

    @Override
    public void preStore(InstanceLifecycleEvent event) {
        invokeMethods(event.getPersistentInstance(), InstanceLifecycleListenerType.PRE_STORE);
    }

    @Override
    public void postStore(InstanceLifecycleEvent event) {
        invokeMethods(event.getPersistentInstance(), InstanceLifecycleListenerType.POST_STORE);
    }

    @Override
    public void preDelete(InstanceLifecycleEvent event) {
        invokeMethods(event.getPersistentInstance(), InstanceLifecycleListenerType.PRE_DELETE);
    }

    @Override
    public void postDelete(InstanceLifecycleEvent event) {
        invokeMethods(event.getPersistentInstance(), InstanceLifecycleListenerType.POST_DELETE);
    }

    @Override
    public void postLoad(InstanceLifecycleEvent event) {
        invokeMethods(event.getPersistentInstance(), InstanceLifecycleListenerType.POST_LOAD);
    }

    /**
     * Invokes service's methods which listen to concrete type of persistence events. A service class
     * is searched for the services exposed by OSGi. If it was found, method is invoked in the same transaction
     * as the event.
     *
     * @param event the persistence event
     * @param type the type of listener
     */
    private void invokeMethods(Object event, InstanceLifecycleListenerType type) throws JdoListenerInvocationException {
        if (jdoListenerRegistryService == null) {
            this.jdoListenerRegistryService = OSGiServiceUtils.findService(bundleContext, JdoListenerRegistryService.class);
        }

        String entity = event.getClass().getName();
        List<MotechLifecycleListener> listeners = jdoListenerRegistryService.getListeners(entity, type);

        for (MotechLifecycleListener listener : listeners) {
            Set<String> methods = jdoListenerRegistryService.getMethods(listener, type);
            if (methods != null && !methods.isEmpty()) {
                Class clazz = listener.getService();
                Object service = OSGiServiceUtils.findService(bundleContext, clazz);
                if (service != null) {
                    Class<?> serviceClass = service.getClass();
                    for (String methodName : methods) {
                        try {
                            MethodUtils.invokeMethod(service, methodName, event);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                            throw new JdoListenerInvocationException(String.format("There was an error invoking the method %s " +
                                    "from %s", methodName, serviceClass.getName()), ex);
                        }
                    }
                }  else {
                    throw new JdoListenerInvocationException(String.format("JDO instance lifecycle event has taken place and is " +
                            "tracked by the %s, but the OSGi service for this class cannot be found.",  clazz.getName()));
                }
            }
        }

    }
}
