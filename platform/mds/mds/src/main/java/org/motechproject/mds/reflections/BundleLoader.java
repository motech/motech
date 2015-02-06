package org.motechproject.mds.reflections;

import javassist.ByteArrayClassPath;
import org.apache.commons.io.IOUtils;
import org.motechproject.mds.ex.loader.LoaderException;
import org.motechproject.mds.javassist.JavassistHelper;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.util.Loader;
import org.motechproject.mds.util.MDSClassLoader;
import org.osgi.framework.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * The <code>BundleLoader</code> is a implementation of the {@link org.motechproject.mds.util.Loader}
 * interface. It takes class information directly from bundle resources. In the same way are taken
 * missing classes.
 *
 * @see org.motechproject.mds.util.Loader
 */
public class BundleLoader extends Loader<String> {
    private MDSClassLoader classLoader;
    private Bundle bundle;

    public BundleLoader(Bundle bundle) {
        this.classLoader = MDSClassLoader.getStandaloneInstance();
        this.bundle = bundle;
    }

    @Override
    public Class<?> getClassDefinition(String className) {
        return load(className, true);
    }

    @Override
    public void doWhenClassNotFound(String className) {
        load(className, true);
    }

    private Class<?> load(String className, boolean addToClassPool) {
        Class<?> result = null;

        String classpath = JavassistHelper.toClassPath(className);
        URL classResource = bundle.getResource(classpath);

        if (classResource != null) {
            loadInterfacesAndSuperClass(className);

            try (InputStream in = classResource.openStream()) {
                byte[] bytecode = IOUtils.toByteArray(in);

                result = classLoader.safeDefineClass(className, bytecode);

                if (addToClassPool) {
                    ByteArrayClassPath classPath = new ByteArrayClassPath(className, bytecode);
                    MotechClassPool.getDefault().appendClassPath(classPath);
                }
            } catch (IOException e) {
                throw new LoaderException(e);
            }
        }

        return result;
    }

    private void loadInterfacesAndSuperClass(String className) {
        try {
            Class<?> definition = bundle.loadClass(className);

            if (JavassistHelper.inheritsFromCustomClass(definition)
                    && !definition.isAnonymousClass()) {
                Class<?> clazz = loadClass(definition.getSuperclass().getName());
                loadFieldsAndMethodsOfClass(clazz);
            }

            for (Class interfaceClass : definition.getInterfaces()) {
                Class<?> clazz = loadClass(interfaceClass.getName());
                loadFieldsAndMethodsOfClass(clazz);
            }
        } catch (ClassNotFoundException e) {
            throw new LoaderException(e);
        }
    }

}
