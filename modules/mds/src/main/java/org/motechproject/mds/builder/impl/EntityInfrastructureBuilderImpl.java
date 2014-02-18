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
    public List<ClassData> buildInfrastructure(String entityClassName) {
        List<ClassData> list = new ArrayList<>();

        String repositoryClassName = ClassName.getRepositoryName(entityClassName);
        if (!existsInClassPath(repositoryClassName)) {
            byte[] repositoryCode = getRepositoryCode(repositoryClassName, entityClassName);
            list.add(new ClassData(repositoryClassName, repositoryCode));
        }

        String interfaceClassName = ClassName.getInterfaceName(entityClassName);
        if (!existsInClassPath(interfaceClassName)) {
            byte[] interfaceCode = getInterfaceCode(interfaceClassName, entityClassName);
            list.add(new ClassData(interfaceClassName, interfaceCode));
        }

        String serviceClassName = ClassName.getServiceName(entityClassName);
        if (!existsInClassPath(serviceClassName)) {
            byte[] serviceCode = getServiceCode(serviceClassName, interfaceClassName, entityClassName);
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

    private byte[] getRepositoryCode(String repositoryClassName, String typeName) {
        try {
            CtClass superClass = classPool.getCtClass(REPOSITORY_BASE_CLASS);
            superClass.setGenericSignature(getGenericSignature(typeName));

            CtClass subClass = classPool.makeClass(repositoryClassName, superClass);

            String repositoryName = ClassName.getSimpleName(repositoryClassName);
            String constructorAsString = String.format(
                    "public %s(){super(%s.class);}", repositoryName, typeName
            );
            CtConstructor constructor = CtNewConstructor.make(constructorAsString, subClass);

            subClass.addConstructor(constructor);
            return subClass.toBytecode();
        } catch (NotFoundException | CannotCompileException | IOException e) {
            throw new EntityInfrastructureException(e);
        }
    }

    private byte[] getInterfaceCode(String interfaceClassName, String typeName) {
        try {
            CtClass superInterface = classPool.getCtClass(SERVICE_BASE_CLASS);
            superInterface.setGenericSignature(getGenericSignature(typeName));

            return classPool.makeInterface(interfaceClassName, superInterface).toBytecode();
        } catch (NotFoundException | IOException | CannotCompileException e) {
            throw new EntityInfrastructureException(e);
        }
    }

    private byte[] getServiceCode(String serviceClassName, String interfaceClassName,
                                  String typeName) {
        try {
            CtClass superClass = classPool.getCtClass(SERVICE_IMPL_BASE_CLASS);
            superClass.setGenericSignature(getGenericSignature(typeName));

            CtClass serviceInterface = classPool.getCtClass(interfaceClassName);

            CtClass subClass = classPool.makeClass(serviceClassName);
            subClass.setSuperclass(superClass);
            subClass.addInterface(serviceInterface);

            return subClass.toBytecode();
        } catch (NotFoundException | IOException | CannotCompileException e) {
            throw new EntityInfrastructureException(e);
        }
    }

    private static String getGenericSignature(String typeName) {
        ClassType classType = new ClassType(typeName);
        TypeParameter parameter = new TypeParameter("T", classType, null);
        ClassSignature sig = new ClassSignature(new TypeParameter[]{parameter});

        return sig.encode();
    }
}
