package org.motechproject.mds.javassist;


import org.apache.commons.lang.StringUtils;

public final class JavassistHelper {

    public static String genericFieldSignature(Class<?> typeClass, Class<?> genericParam) {
        return genericFieldSignature(typeClass.getName(), genericParam.getName());
    }

    public static String genericFieldSignature(String typeClass, String genericParam) {
        return "L" + toClassPath(typeClass) + "<L" + toClassPath(genericParam) + ";>;";
    }

    public static String toClassPath(Class<?> clazz) {
        return toClassPath(clazz.getName());
    }

    public static String toClassPath(String clazz) {
        return StringUtils.replace(clazz, ".", "/");
    }

    private JavassistHelper() {
    }
}
