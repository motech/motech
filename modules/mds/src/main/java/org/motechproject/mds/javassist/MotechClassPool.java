package org.motechproject.mds.javassist;

import javassist.ClassClassPath;
import javassist.ClassPool;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.motechproject.mds.builder.ClassData;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.impl.DefaultMotechDataService;
import org.motechproject.mds.util.ClassName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the javasisst classpool, enriched by motech classes. All predefined additions to the ClassPool
 * should take place here. The classpool should also be retrieved using this class, in order to be sure that the a
 * initialization took place.
 */
public final class MotechClassPool {

    private static final ClassPool POOL;

    private static Map<String, ClassData> classData = new HashMap<>();
    private static Map<String, String> serviceInterfaces = new HashMap<>();

    static {
        POOL = ClassPool.getDefault();

        POOL.appendClassPath(new ClassClassPath(MotechDataRepository.class));
        POOL.appendClassPath(new ClassClassPath(MotechDataService.class));
        POOL.appendClassPath(new ClassClassPath(DefaultMotechDataService.class));
    }

    public static ClassPool getDefault() {
        return POOL;
    }

    public static ClassData getEnhancedClassData(String className) {
        return classData.get(className);
    }

    public static void registerEnhancedClassData(ClassData enhancedClassData) {
        classData.put(enhancedClassData.getClassName(), enhancedClassData);
    }

    public static void unregisterEnhancedData(String className) {
        classData.remove(className);
    }

    public static Collection<ClassData> getEnhancedClasses(boolean includeInerfaces) {
        Collection<ClassData> values = new ArrayList<>(classData.values());
        if (!includeInerfaces) {
            CollectionUtils.filter(values, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return !((ClassData) object).isInterfaceClass();
                }
            });
        }
        return values;
    }

    public static void clearEnhancedData() {
        classData.clear();
    }

    public static String getServiceInterface(String className) {
        return serviceInterfaces.get(className);
    }

    public static void registerServiceInterface(String className, String interfaceName) {
        serviceInterfaces.put(className, interfaceName);
    }

    public static boolean isServiceInterfaceRegistered(String className) {
        return serviceInterfaces.containsKey(className);
    }

    public static Collection<String> registeredInterfaces() {
        return serviceInterfaces.values();
    }

    public static String getInterfaceName(String className) {
        // interface name can come from a dde interface
        if (isServiceInterfaceRegistered(className)) {
            return getServiceInterface(className);
        } else {
            return ClassName.getInterfaceName(className);
        }
    }

    // repository name and service impl names come only from us

    public static String getRepositoryName(String className) {
        return ClassName.getRepositoryName(className);
    }

    public static String getServiceImplName(String className) {
        return ClassName.getServiceName(className);
    }

    private MotechClassPool() {
    }
}
