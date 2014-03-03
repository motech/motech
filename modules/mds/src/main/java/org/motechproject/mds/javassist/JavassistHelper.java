package org.motechproject.mds.javassist;


import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.osgi.framework.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Helper class for javassist related tasks. Helps with generic signature generation,
 * plus methods related with analyzing and loading javassist class representations.
 */
public final class JavassistHelper {

    public static String genericSignature(Class<?> typeClass, Class<?> genericParam) {
        return genericSignature(typeClass.getName(), genericParam.getName());
    }

    public static String genericSignature(String typeClass, String genericParam) {
        return "L" + replaceDotsWithSlashes(typeClass) + "<" + toGenericParam(genericParam) + ">;";
    }

    public static String toClassPath(Class<?> clazz) {
        return toClassPath(clazz.getName());
    }

    public static String toClassPath(String clazz) {
        return replaceDotsWithSlashes(clazz) + ".class";
    }

    public static String toGenericParam(Class<?> clazz) {
        return toGenericParam(clazz.getName());
    }

    public static String toGenericParam(String clazz) {
        return String.format("L%s;", replaceDotsWithSlashes(clazz));
    }

    public static CtClass loadClass(Bundle bundle, String className, ClassPool classPool) throws IOException {
        // return if already loaded
        CtClass existing = classPool.getOrNull(className);
        if (existing != null) {
            return existing;
        }

        // load from the bundle
        CtClass clazz = null;

        URL classUrl = bundle.getResource(toClassPath(className));
        if (classUrl != null) {
            try (InputStream classInputStream = classUrl.openStream()) {
                clazz = classPool.makeClass(classInputStream);
            }
        }

        return clazz;
    }

    public static boolean containsDeclaredField(CtClass ctClass, String fieldName) {
        boolean found = false;

        CtField[] declaredFields = ctClass.getDeclaredFields();

        if (ArrayUtils.isNotEmpty(declaredFields)) {
            for (CtField field : declaredFields) {
                if (StringUtils.equals(fieldName, field.getName())) {
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    public static boolean hasInterface(CtClass ctClass, CtClass ctInterface) throws NotFoundException {
        CtClass[] interfaces = ctClass.getInterfaces();
        if (ArrayUtils.isNotEmpty(interfaces)) {
            for (CtClass declaredInterface : interfaces) {
                if (declaredInterface.equals(ctInterface)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String replaceDotsWithSlashes(String str) {
        return StringUtils.replace(str, ".", "/");
    }

    private JavassistHelper() {
    }
}
