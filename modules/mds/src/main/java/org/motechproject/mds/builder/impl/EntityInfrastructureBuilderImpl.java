package org.motechproject.mds.builder.impl;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import org.motechproject.mds.builder.ClassData;
import org.motechproject.mds.builder.EntityInfrastructureBuilder;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.ex.EntityInfrastructureException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.util.ClassName;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
    public List<ClassData> buildInfrastructure(Entity entity) {
        List<ClassData> list = new ArrayList<>();

        String repositoryClassName = ClassName.getRepositoryName(entity.getClassName());
        if (!existsInClassPath(repositoryClassName)) {
            byte[] repositoryCode = getRepositoryCode(repositoryClassName, entity.getClassName());
            list.add(new ClassData(repositoryClassName, repositoryCode));
        }

        String interfaceClassName = ClassName.getInterfaceName(entity.getClassName());
        if (!existsInClassPath(interfaceClassName)) {
            byte[] interfaceCode = getInterfaceCode(interfaceClassName, entity.getClassName(), entity);
            list.add(new ClassData(interfaceClassName, interfaceCode));
        }

        String serviceClassName = ClassName.getServiceName(entity.getClassName());
        if (!existsInClassPath(serviceClassName)) {
            byte[] serviceCode = getServiceCode(serviceClassName, interfaceClassName, entity);
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

    private byte[] getInterfaceCode(String interfaceClassName, String typeName, Entity entity) {
        try {
            CtClass superInterface = classPool.getCtClass(SERVICE_BASE_CLASS);
            superInterface.setGenericSignature(getGenericSignature(typeName));

            CtClass newInterface = classPool.makeInterface(interfaceClassName, superInterface);

            for (Lookup lookup : entity.getLookups()) {
                CtMethod lookupMethod = CtNewMethod.make(generateLookupInterface(lookup, entity.getClassName()), newInterface);
                newInterface.addMethod(lookupMethod);
            }

            return newInterface.toBytecode();
        } catch (NotFoundException | IOException | CannotCompileException e) {
            throw new EntityInfrastructureException(e);
        }
    }

    private byte[] getServiceCode(String serviceClassName, String interfaceClassName,
                                  Entity entity) {
        try {
            CtClass superClass = classPool.getCtClass(SERVICE_IMPL_BASE_CLASS);
            superClass.setGenericSignature(getGenericSignature(entity.getClassName()));

            CtClass serviceInterface = classPool.getCtClass(interfaceClassName);

            CtClass subClass = classPool.makeClass(serviceClassName);
            subClass.setSuperclass(superClass);

            subClass.addInterface(serviceInterface);

            for (Lookup lookup : entity.getLookups()) {
                CtMethod lookupMethod = CtNewMethod.make(generateLookup(lookup, entity.getClassName()), subClass);
                subClass.addMethod(lookupMethod);
            }

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

    private String generateLookupInterface(Lookup lookup, String className) {
        StringBuilder sb = new StringBuilder("");

        if (lookup.isSingleObjectReturn()) {
            sb.append(className);
        } else {
            sb.append("java.util.List");
        }
        sb.append(" ").append(lookup.getLookupName()).append("(");

        Iterator it = lookup.getFields().iterator();
        while (it.hasNext()) {
            Field field = (Field) it.next();
            sb.append(field.getType().getTypeClassName()).append(" ").append(field.getDisplayName());

            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(");");

        return sb.toString();
    }

    private String generateLookup(Lookup lookup, String className) {
        StringBuilder sb = new StringBuilder("public ");
        StringBuilder paramsSb = new StringBuilder("new java.lang.String[] {");
        StringBuilder valuesSb = new StringBuilder("new java.lang.Object[] {");

        if (lookup.isSingleObjectReturn()) {
            sb.append(className);
        } else {
            sb.append("java.util.List");
        }
        sb.append(" ").append(lookup.getLookupName()).append("(");

        Iterator it = lookup.getFields().iterator();
        while (it.hasNext()) {
            Field field = (Field) it.next();

            paramsSb.append("\"").append(field.getDisplayName()).append("\"");

            valuesSb.append(field.getDisplayName());

            sb.append(field.getType().getTypeClassName()).append(" ").append(field.getDisplayName());

            if (it.hasNext()) {
                sb.append(", ");
                paramsSb.append(", ");
                valuesSb.append(", ");
            }
        }
        paramsSb.append("}");
        valuesSb.append("}");
        sb.append(") {return ");

        if (lookup.isSingleObjectReturn()) {
            sb.append("(").append(className).append(")");
        }

        sb.append("retrieveAll(").append(paramsSb.toString()).append(", ").append(valuesSb.toString()).append(")");
        if (lookup.isSingleObjectReturn()) {
            sb.append(".get(0)");
        }
        sb.append(";}");
        return sb.toString();
    }

}
