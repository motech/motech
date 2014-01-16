package org.motechproject.mds.builder;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import org.motechproject.mds.domain.ClassMapping;
import org.motechproject.mds.ex.EntityInfrastructureException;
import org.motechproject.mds.javassist.MotechClassPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javassist.bytecode.SignatureAttribute.ClassSignature;
import static javassist.bytecode.SignatureAttribute.ClassType;
import static javassist.bytecode.SignatureAttribute.TypeParameter;

/**
 * The <code>EntityInfrastructureBuilder</code> class is responsible to create for a given entity
 * repository, interface and service classes. These classes are created only if they are not present
 * in classpath.
 */
public final class EntityInfrastructureBuilder {
    private static final String BASE_PACKAGE = "org.motechproject.mds";
    private static final String REPOSITORY_PACKAGE = BASE_PACKAGE + ".repository";
    private static final String SERVICE_PACKAGE = BASE_PACKAGE + ".service";
    private static final String SERVICE_IMPL_PACKAGE = SERVICE_PACKAGE + ".impl";

    private static final String REPOSITORY_BASE_CLASS = REPOSITORY_PACKAGE + ".MotechDataRepository";
    private static final String SERVICE_BASE_CLASS = SERVICE_PACKAGE + ".MotechDataService";
    private static final String SERVICE_IMPL_BASE_CLASS = SERVICE_IMPL_PACKAGE + ".DefaultMotechDataService";

    private static final ClassPool POOL = MotechClassPool.getDefault();

    private EntityInfrastructureBuilder() {
    }

    public static List<ClassMapping> create(ClassLoader loader, Class<?> entityClass) {
        List<ClassMapping> list = new ArrayList<>();
        String entityName = entityClass.getSimpleName();

        String repositoryClassName = getRepositoryName(entityName);
        if (!existsInClassPath(loader, repositoryClassName)) {
            byte[] repositoryCode = getRepositoryCode(repositoryClassName, entityClass);
            list.add(new ClassMapping(repositoryClassName, repositoryCode));
        }

        String interfaceClassName = getInterfaceName(entityName);
        if (!existsInClassPath(loader, interfaceClassName)) {
            byte[] interfaceCode = getInterfaceCode(interfaceClassName, entityClass);
            list.add(new ClassMapping(interfaceClassName, interfaceCode));
        }

        String serviceClassName = getServiceName(entityName);
        if (!existsInClassPath(loader, serviceClassName)) {
            byte[] serviceCode = getServiceCode(serviceClassName, interfaceClassName, entityClass);
            list.add(new ClassMapping(serviceClassName, serviceCode));
        }

        return list;
    }

    private static boolean existsInClassPath(ClassLoader loader, String className) {
        boolean exists;

        try {
            exists = loader.loadClass(className) != null;
        } catch (ClassNotFoundException e) {
            exists = false;
        }

        return exists;
    }

    private static byte[] getRepositoryCode(String repositoryClassName, Class<?> type) {
        try {
            CtClass superClass = POOL.getCtClass(REPOSITORY_BASE_CLASS);
            superClass.setGenericSignature(getGenericSignature(type));

            CtClass subClass = POOL.makeClass(repositoryClassName, superClass);

            String repositoryName = getSimpleName(repositoryClassName);
            String constructorAsString = String.format(
                    "public %s(){super(%s.class);}", repositoryName, type.getName()
            );
            CtConstructor constructor = CtNewConstructor.make(constructorAsString, subClass);

            subClass.addConstructor(constructor);
            return subClass.toBytecode();
        } catch (NotFoundException | CannotCompileException | IOException e) {
            throw new EntityInfrastructureException(e);
        }
    }

    private static byte[] getInterfaceCode(String interfaceClassName, Class<?> type) {
        try {
            CtClass superInterface = POOL.getCtClass(SERVICE_BASE_CLASS);
            superInterface.setGenericSignature(getGenericSignature(type));

            return POOL.makeInterface(interfaceClassName, superInterface).toBytecode();
        } catch (NotFoundException | IOException | CannotCompileException e) {
            throw new EntityInfrastructureException(e);
        }
    }

    private static byte[] getServiceCode(String serviceClassName, String interfaceClassName,
                                         Class<?> type) {
        try {
            CtClass superClass = POOL.getCtClass(SERVICE_IMPL_BASE_CLASS);
            superClass.setGenericSignature(getGenericSignature(type));

            CtClass serviceInterface = POOL.getCtClass(interfaceClassName);

            CtClass subClass = POOL.makeClass(serviceClassName);
            subClass.setSuperclass(superClass);
            subClass.addInterface(serviceInterface);

            return subClass.toBytecode();
        } catch (NotFoundException | IOException | CannotCompileException e) {
            throw new EntityInfrastructureException(e);
        }
    }

    private static String getGenericSignature(Class<?> type) {
        ClassType classType = new ClassType(type.getName());
        TypeParameter parameter = new TypeParameter("T", classType, null);
        ClassSignature sig = new ClassSignature(new TypeParameter[]{parameter});

        return sig.encode();
    }

    private static String getSimpleName(String className) {
        int idx = className.lastIndexOf('.');
        return idx < 0 ? className : className.substring(idx + 1);
    }

    private static String getRepositoryName(String entityName) {
        return String.format("%s.All%ss", REPOSITORY_PACKAGE, entityName);
    }

    private static String getInterfaceName(String entityName) {
        return String.format("%s.%sService", SERVICE_PACKAGE, entityName);
    }

    private static String getServiceName(String entityName) {
        return String.format("%s.%sServiceImpl", SERVICE_IMPL_PACKAGE, entityName);
    }

}
