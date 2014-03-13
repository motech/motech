package org.motechproject.mds.jdo;

import org.eclipse.gemini.blueprint.util.BundleDelegatingClassLoader;
import org.motechproject.mds.builder.MDSClassLoader;
import org.springframework.orm.jdo.JdoTransactionManager;
import org.springframework.transaction.TransactionDefinition;

/**
 * We override springs transaction for classloader control. We store context classloaders
 * as thread local variables, and switch them with the MDS classloader for the transaction.
 * Since we only allow operations in transactions, this entry point for classloader switching is enough.
 */
public class MdsTransactionManager extends JdoTransactionManager {

    private static final long serialVersionUID = 3817917722565508554L;

    private ThreadLocal<ClassLoader> contextClassLoader = new ThreadLocal<>();

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

        // a case for Bundle ITs, we do not switch the classloaders
        if (!(currentClassLoader instanceof BundleDelegatingClassLoader)) {
            contextClassLoader.set(currentClassLoader);
            Thread.currentThread().setContextClassLoader(MDSClassLoader.getInstance());
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
}
