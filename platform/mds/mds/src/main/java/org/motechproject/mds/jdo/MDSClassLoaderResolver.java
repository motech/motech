package org.motechproject.mds.jdo;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ClassLoaderResolverImpl;
import org.datanucleus.exceptions.ClassNotResolvedException;

public class MDSClassLoaderResolver extends ClassLoaderResolverImpl implements ClassLoaderResolver {

    public MDSClassLoaderResolver() {
        this(null);
    }

    public MDSClassLoaderResolver(ClassLoader pmLoader) {
        super(pmLoader);
    }

    @Override
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
