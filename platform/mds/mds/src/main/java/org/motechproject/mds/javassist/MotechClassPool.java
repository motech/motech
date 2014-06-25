package org.motechproject.mds.javassist;

import javassist.ClassClassPath;
import javassist.ClassPool;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.service.DefaultMotechDataService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.ClassName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class holds the javasisst classpool, enriched by motech classes. All predefined additions to the ClassPool
 * should take place here. The classpool should also be retrieved using this class, in order to be sure that the a
 * initialization took place.
 */
public final class MotechClassPool {

    private static final ClassPool POOL;

    private static Map<String, ClassData> classData = new HashMap<>();
    private static Map<String, ClassData> historyClassData = new HashMap<>();
    private static Map<String, ClassData> trashClassData = new HashMap<>();
    private static Map<String, String> serviceInterfaces = new HashMap<>();
    private static Set<String> enums = new HashSet<>();
    private static Set<String> readyDDE = new HashSet<>();

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
        trashClassData.clear();
        historyClassData.clear();
    }

    public static String getServiceInterface(String className) {
        return serviceInterfaces.get(className);
    }

    public static void registerServiceInterface(String className, String interfaceName) {
        serviceInterfaces.put(className, interfaceName);
    }

    public static void registerEnum(String enumName) {
        enums.add(enumName);
    }

    public static boolean isServiceInterfaceRegistered(String className) {
        return serviceInterfaces.containsKey(className);
    }

    public static Collection<String> registeredInterfaces() {
        return serviceInterfaces.values();
    }

    public static Collection<String> registeredEnums() {
        return enums;
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

    public static void registerHistoryClassData(ClassData cData) {
        historyClassData.put(cData.getClassName(), cData);
    }

    public static ClassData getHistoryClassData(String className) {
        return historyClassData.get(ClassName.getHistoryClassName(className));
    }

    public static void registerTrashClassData(ClassData cData) {
        trashClassData.put(cData.getClassName(), cData);
    }

    public static ClassData getTrashClassData(String className) {
        return trashClassData.get(ClassName.getTrashClassName(className));
    }

    public static void registerDDE(String className) {
        readyDDE.add(className);
    }

    public static boolean isDDEReady(String className) {
        return readyDDE.contains(className);
    }

    private MotechClassPool() {
    }
}
