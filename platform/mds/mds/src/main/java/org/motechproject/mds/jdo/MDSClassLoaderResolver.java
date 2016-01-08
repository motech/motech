package org.motechproject.mds.jdo;

import org.apache.felix.framework.BundleWiringImpl;
import org.datanucleus.ClassLoaderResolver;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.motechproject.mds.helper.MdsBundleHelper;
import org.motechproject.mds.util.Constants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * This is a wrapper for {@link org.motechproject.mds.jdo.MDSClassLoaderResolverImpl}.
 * All calls for the {@link org.datanucleus.ClassLoaderResolver} interface are passed to
 * the current instance of the ClassLoaderResolver implementation.
 * When we hit a NullPointerException originating in Felix, we can determine it is due to
 * a synchronization bug after bundle updates - as a result of this DataNucleus has passed us
 * ClassLoaders from the former Bundle version. In that case we reload the instance passing it the
 * ClassLoaders from the new bundle.
 */
public class MDSClassLoaderResolver implements ClassLoaderResolver {

    private MDSClassLoaderResolverImpl instance;

    public MDSClassLoaderResolver() {
        instance = new MDSClassLoaderResolverImpl();
    }

    public MDSClassLoaderResolver(ClassLoader pmLoader) {
        instance = new MDSClassLoaderResolverImpl(pmLoader);
    }

    @Override
    public Class classForName(String name, ClassLoader primary) {
        return instance.classForName(name, primary);
    }

    @Override
    public Class classForName(String name, ClassLoader primary, boolean initialize) {
        return instance.classForName(name, primary, initialize);
    }

    @Override
    public Class classForName(String name) {
        return instance.classForName(name);
    }

    @Override
    public Class classForName(String name, boolean initialize) {
        return instance.classForName(name, initialize);
    }

    @Override
    public boolean isAssignableFrom(String className, Class clazz) {
        return instance.isAssignableFrom(className, clazz);
    }

    @Override
    public boolean isAssignableFrom(Class clazz, String className) {
        return instance.isAssignableFrom(clazz, className);
    }

    @Override
    public boolean isAssignableFrom(String className1, String className2) {
        return instance.isAssignableFrom(className1, className2);
    }

    @Override
    public Enumeration<URL> getResources(String resourceName, ClassLoader primary) throws IOException {
        try {
            // MOTECH-1788 fix
            // We want to get package.jdo resource only with MDS bundle ClassLoader,
            // because only this ClassLoader has actual version of MDS entities classes.
            if (resourceName.contains("package.jdo") && !MdsBundleHelper.isMdsClassLoader(primary)) {
                return Collections.enumeration(new ArrayList<URL>());
            }
            return instance.getResources(resourceName, primary);
        } catch (NullPointerException e) {
            // MOTECH-1164 fix
            // This is a bug originating in Felix after bundle updates
            // Datanucleus passes us classloaders coming from old bundles,
            // so we have to retrieve the new ClassLoader and recreate the actual
            // resolver instance.
            ClassLoader newCl = getNewClassLoader();
            instance = new MDSClassLoaderResolverImpl(newCl);
            return instance.getResources(resourceName, newCl);
        }
    }

    @Override
    public URL getResource(String resourceName, ClassLoader primary) {
        try {
            return instance.getResource(resourceName, primary);
        } catch (NullPointerException e) {
            // MOTECH-1164 fix
            // This is a bug originating in Felix after bundle updates
            // Datanucleus passes us classloaders coming from old bundles,
            // so we have to retrieve the new ClassLoader and recreate the actual
            // resolver instance.
            ClassLoader newCl = getNewClassLoader();
            instance = new MDSClassLoaderResolverImpl(newCl);
            return instance.getResource(resourceName, newCl);
        }
    }

    @Override
    public void setRuntimeClassLoader(ClassLoader loader) {
        instance.setRuntimeClassLoader(loader);
    }

    @Override
    public void registerUserClassLoader(ClassLoader loader) {
        instance.registerUserClassLoader(loader);
    }

    @Override
    public void setPrimary(ClassLoader primary) {
        instance.setPrimary(primary);
    }

    @Override
    public void unsetPrimary() {
        instance.unsetPrimary();
    }

    private ClassLoader getNewClassLoader() {
        BundleContext bc = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
        Bundle bundle = OsgiBundleUtils.findBundleBySymbolicName(bc, Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME);
        BundleWiringImpl bundleWiring = (BundleWiringImpl) bundle.adapt(BundleWiring.class);
        return bundleWiring.getClassLoader();
    }
}
