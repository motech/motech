package org.motechproject.mds.jdo;

import org.datanucleus.ClassLoaderResolverImpl;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.util.Localiser;
import org.motechproject.mds.util.MDSClassLoader;

/**
 * The main purpose of the <code>MDSClassLoaderResolverImpl</code> class is to avoid situation in which
 * standard datanucleus class loader resolver does not see classes that are saved in database.
 * This is the main implementation that extends the standard ClassLoaderResolverImpl from datanucleus.
 * Due to a synchronization bug in Felix, there are cases when we will instantiate this more then once
 * (after we hit the bug).
 */
class MDSClassLoaderResolverImpl extends ClassLoaderResolverImpl {

    public MDSClassLoaderResolverImpl() {
        this(null);
    }

    public MDSClassLoaderResolverImpl(ClassLoader pmLoader) {
        super(pmLoader);
    }

    @Override
    @SuppressWarnings("PMD.PreserveStackTrace")
    public Class classForName(String name, ClassLoader primary) {
        try {
            return super.classForName(name, resolvePrimaryClassLoader(primary));
        } catch (ClassNotResolvedException e) {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException | NoClassDefFoundError ex) {
                try {
                    return MDSClassLoader.getInstance().loadClass(name);
                } catch (ClassNotFoundException exp) {
                    throw new ClassNotResolvedException(Localiser.msg("001000", name), exp);
                }
            }
        }
    }

    private ClassLoader resolvePrimaryClassLoader(ClassLoader forwarded) {
        // We want to use MDSClassLoader as a last resort class loader only, never as a primary one
        if (forwarded != null && !(forwarded instanceof MDSClassLoader)) {
            return forwarded;
        } else if (contextLoader != null && !(contextLoader instanceof MDSClassLoader)) {
            return contextLoader;
        }
        return forwarded;
    }
}
