package org.motechproject.mds.jdo;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jdo.JdoTransactionManager;
import org.springframework.transaction.TransactionDefinition;

/**
 * We override springs transaction for classloader control. We store context classloaders
 * as thread local variables, and switch them with the bundle classloader for the transaction.
 * Since we only allow operations in transactions, this entry point for classloader switching is enough.
 */
public class MdsTransactionManager extends JdoTransactionManager {

    private static final long serialVersionUID = 3817917722565508554L;

    private ThreadLocal<ClassLoader> contextClassLoader = new ThreadLocal<>();

    private BundleContext bundleContext;

    private ClassLoader bundleClassLoader;

    // this is only used in context ITs
    public void setBundleClassLoader(ClassLoader bundleClassLoader) {
        this.bundleClassLoader = bundleClassLoader;
    }

    public ClassLoader getBundleClassLoader() {
        if (bundleClassLoader == null) {
            Bundle bundle = bundleContext.getBundle();
            bundleClassLoader = bundle.adapt(BundleWiring.class).getClassLoader();
        }
        return bundleClassLoader;
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

        if (currentClassLoader.toString().startsWith("sun.misc.Launcher$AppClassLoader")
                || currentClassLoader.getClass().getName().startsWith("org.apache.catalina")) {
            contextClassLoader.set(currentClassLoader);
            Thread.currentThread().setContextClassLoader(getBundleClassLoader());
        }

        super.doBegin(transaction, definition);
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        try {
            super.doCleanupAfterCompletion(transaction);
        } finally {
            ClassLoader classLoader = contextClassLoader.get();

            if (classLoader != null) {
                Thread.currentThread().setContextClassLoader(classLoader);
            }

            contextClassLoader.remove();
        }
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
