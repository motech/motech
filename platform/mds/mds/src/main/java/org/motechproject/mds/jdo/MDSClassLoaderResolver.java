package org.motechproject.mds.jdo;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ClassLoaderResolverImpl;
import org.datanucleus.exceptions.ClassNotResolvedException;

/**
 * The main purpose of the <code>MDSClassLoaderResolver</code> class is to avoid situation in which
 * standard datanucleus class loader resolver does not see classes that are saved in database.
 */
public class MDSClassLoaderResolver extends ClassLoaderResolverImpl implements ClassLoaderResolver {

    public MDSClassLoaderResolver() {
        this(null);
    }

    public MDSClassLoaderResolver(ClassLoader pmLoader) {
        super(pmLoader);
    }

    @Override
    @SuppressWarnings("PMD.PreserveStackTrace")
    public Class classForName(String name, ClassLoader primary) {
        try {
            return super.classForName(name, primary);
        } catch (ClassNotResolvedException e) {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException | NoClassDefFoundError ex) {
                throw new ClassNotResolvedException(LOCALISER.msg("001000", name), ex);
            }
        }
    }

}
