package org.motechproject.mds.builder;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import org.apache.commons.lang.ArrayUtils;
import org.motechproject.mds.ex.EntityInfrastructureException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.util.ClassName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javassist.bytecode.SignatureAttribute.ClassSignature;
import static javassist.bytecode.SignatureAttribute.ClassType;
import static javassist.bytecode.SignatureAttribute.TypeParameter;
import static org.motechproject.mds.constants.Constants.Packages;

/**
 * The <code>EntityInfrastructureBuilder</code> class is responsible to create for a given entity
 * repository, interface and service classes. These classes are created only if they are not present
 * in classpath.
 */
public final class EntityInfrastructureBuilder {
    public static final String REPOSITORY_BASE_CLASS = Packages.REPOSITORY + ".MotechDataRepository";
    public static final String SERVICE_BASE_CLASS = Packages.SERVICE + ".MotechDataService";
    public static final String SERVICE_IMPL_BASE_CLASS = Packages.SERVICE_IMPL + ".DefaultMotechDataService";

    private static final ClassPool POOL = MotechClassPool.getDefault();

    private EntityInfrastructureBuilder() {
    }

    public static List<ClassMapping> create(ClassLoader loader, Class<?> entityClass) {
        List<ClassMapping> list = new ArrayList<>();
        String className = entityClass.getName();

        String repositoryClassName = ClassName.getRepositoryName(className);
        if (!existsInClassPath(loader, repositoryClassName)) {
            byte[] repositoryCode = getRepositoryCode(repositoryClassName, entityClass);
            list.add(new ClassMapping(repositoryClassName, repositoryCode));
        }

        String interfaceClassName = ClassName.getInterfaceName(className);
        if (!existsInClassPath(loader, interfaceClassName)) {
            byte[] interfaceCode = getInterfaceCode(interfaceClassName, entityClass);
            list.add(new ClassMapping(interfaceClassName, interfaceCode));
        }

        String serviceClassName = ClassName.getServiceName(className);
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

            String repositoryName = ClassName.getSimpleName(repositoryClassName);
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

    public static class ClassMapping {
        private String className;
        private byte[] bytecode;

        public ClassMapping(String className, byte[] bytecode) {
            this.className = className;
            this.bytecode = ArrayUtils.isNotEmpty(bytecode)
                    ? Arrays.copyOf(bytecode, bytecode.length)
                    : new byte[0];
        }

        public String getClassName() {
            return className;
        }

        public byte[] getBytecode() {
            return Arrays.copyOf(bytecode, getLength());
        }

        public int getLength() {
            return bytecode.length;
        }
    }
}
