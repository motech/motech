package org.motechproject.mds.builder.impl;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import org.motechproject.mds.builder.ClassData;
import org.motechproject.mds.builder.EntityInfrastructureBuilder;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.ex.EntityInfrastructureException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.util.ClassName;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javassist.bytecode.SignatureAttribute.ClassSignature;
import static javassist.bytecode.SignatureAttribute.ClassType;
import static javassist.bytecode.SignatureAttribute.TypeParameter;
import static org.motechproject.mds.util.Constants.Packages;

/**
 * The <code>EntityInfrastructureBuilder</code> class is responsible for building infrastructure for a given entity:
 * repository, interface and service classes. These classes are created only if they are not present
 * in the classpath. This implementation uses javassist in order to construct the classes.
 */
@Component
public class EntityInfrastructureBuilderImpl implements EntityInfrastructureBuilder {
    public static final String REPOSITORY_BASE_CLASS = Packages.REPOSITORY + ".MotechDataRepository";
    public static final String SERVICE_BASE_CLASS = Packages.SERVICE + ".MotechDataService";
    public static final String SERVICE_IMPL_BASE_CLASS = Packages.SERVICE_IMPL + ".DefaultMotechDataService";

    private final ClassPool classPool = MotechClassPool.getDefault();

    @Override
    public List<ClassData> buildInfrastructure(Class<?> entityClass) {
        List<ClassData> list = new ArrayList<>();
        String className = entityClass.getName();

        String repositoryClassName = ClassName.getRepositoryName(className);
        if (!existsInClassPath(repositoryClassName)) {
            byte[] repositoryCode = getRepositoryCode(repositoryClassName, entityClass);
            list.add(new ClassData(repositoryClassName, repositoryCode));
        }

        String interfaceClassName = ClassName.getInterfaceName(className);
        if (!existsInClassPath(interfaceClassName)) {
            byte[] interfaceCode = getInterfaceCode(interfaceClassName, entityClass);
            list.add(new ClassData(interfaceClassName, interfaceCode));
        }

        String serviceClassName = ClassName.getServiceName(className);
        if (!existsInClassPath(serviceClassName)) {
            byte[] serviceCode = getServiceCode(serviceClassName, interfaceClassName, entityClass);
            list.add(new ClassData(serviceClassName, serviceCode));
        }

        return list;
    }

    private static boolean existsInClassPath(String className) {
        boolean exists;

        try {
            exists = MDSClassLoader.getInstance().loadClass(className) != null;
        } catch (ClassNotFoundException e) {
            exists = false;
        }

        return exists;
    }

    private byte[] getRepositoryCode(String repositoryClassName, Class<?> type) {
        try {
            CtClass superClass = classPool.getCtClass(REPOSITORY_BASE_CLASS);
            superClass.setGenericSignature(getGenericSignature(type));

            CtClass subClass = classPool.makeClass(repositoryClassName, superClass);

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

    private byte[] getInterfaceCode(String interfaceClassName, Class<?> type) {
        try {
            CtClass superInterface = classPool.getCtClass(SERVICE_BASE_CLASS);
            superInterface.setGenericSignature(getGenericSignature(type));

            return classPool.makeInterface(interfaceClassName, superInterface).toBytecode();
        } catch (NotFoundException | IOException | CannotCompileException e) {
            throw new EntityInfrastructureException(e);
        }
    }

    private byte[] getServiceCode(String serviceClassName, String interfaceClassName,
                                  Class<?> type) {
        try {
            CtClass superClass = classPool.getCtClass(SERVICE_IMPL_BASE_CLASS);
            superClass.setGenericSignature(getGenericSignature(type));

            CtClass serviceInterface = classPool.getCtClass(interfaceClassName);

            CtClass subClass = classPool.makeClass(serviceClassName);
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
}
