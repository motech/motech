package org.motechproject.mds.javassist;

import javassist.CannotCompileException;
import javassist.CtClass;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.exception.loader.LoaderException;
import org.motechproject.mds.util.Loader;
import org.motechproject.mds.util.MDSClassLoader;

import java.io.IOException;

/**
 * The <code>JavassistLoader</code> is a implementation of the {@link org.motechproject.mds.util.Loader}
 * interface. It takes class information from instance of {@link org.motechproject.mds.domain.ClassData}
 * and the missing classes are taken from {@link org.motechproject.mds.javassist.MotechClassPool}
 *
 * @see org.motechproject.mds.util.Loader
 * @see org.motechproject.mds.domain.ClassData
 * @see org.motechproject.mds.javassist.MotechClassPool
 */
public class JavassistLoader extends Loader<ClassData> {
    private MDSClassLoader classLoader;

    public JavassistLoader(MDSClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Class<?> getClassDefinition(ClassData data) {
        return classLoader.safeDefineClass(data.getClassName(), data.getBytecode());
    }

    @Override
    public void doWhenClassNotFound(String name) {
        CtClass ctClass = MotechClassPool.getDefault().getOrNull(name);

        if (null != ctClass) {
            try {
                ctClass.defrost();
                byte[] bytecode = ctClass.toBytecode();

                loadClass(new ClassData(name, bytecode));
            } catch (IOException | CannotCompileException ex) {
                throw new LoaderException("Unable to load class using Javassist: " + name, ex);
            }
        }
    }

    @Override
    public Class<?> loadClass(ClassData arg) {
        return getClassDefinition(arg);
    }
}
