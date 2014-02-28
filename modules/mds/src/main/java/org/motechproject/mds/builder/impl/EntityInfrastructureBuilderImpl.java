package org.motechproject.mds.builder.impl;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import org.apache.commons.lang.ArrayUtils;
import org.motechproject.mds.builder.ClassData;
import org.motechproject.mds.builder.EntityInfrastructureBuilder;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.ex.EntityInfrastructureException;
import org.motechproject.mds.javassist.JavassistHelper;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.impl.DefaultMotechDataService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.LookupName;
import org.motechproject.mds.util.QueryParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static javassist.bytecode.SignatureAttribute.ClassSignature;
import static javassist.bytecode.SignatureAttribute.ClassType;
import static javassist.bytecode.SignatureAttribute.TypeParameter;

/**
 * The <code>EntityInfrastructureBuilder</code> class is responsible for building infrastructure for a given entity:
 * repository, interface and service classes. These classes are created only if they are not present
 * in the classpath. This implementation uses javassist in order to construct the classes.
 */
@Component
public class EntityInfrastructureBuilderImpl implements EntityInfrastructureBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(EntityInfrastructureBuilderImpl.class);

    private final ClassPool classPool = MotechClassPool.getDefault();

    @Override
    public List<ClassData> buildInfrastructure(Entity entity) {
        List<ClassData> list = new ArrayList<>();


        // create a repository(dao) for the entity
        String repositoryClassName = ClassName.getRepositoryName(entity.getClassName());
        if (!existsInClassPath(repositoryClassName)) {
            byte[] repositoryCode = getRepositoryCode(repositoryClassName, entity.getClassName());
            list.add(new ClassData(repositoryClassName, repositoryCode));
        }

        // create an interface for the service
        String interfaceClassName = ClassName.getInterfaceName(entity.getClassName());
        if (!existsInClassPath(interfaceClassName)) {
            byte[] interfaceCode = getInterfaceCode(interfaceClassName, entity.getClassName(), entity);
            list.add(new ClassData(interfaceClassName, interfaceCode));
        }

        // create the implementation of the service
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
            CtClass superClass = classPool.getCtClass(MotechDataRepository.class.getName());
            superClass.setGenericSignature(getGenericSignature(typeName));

            CtClass subClass = createOrRetrieveClass(repositoryClassName, superClass);

            String repositoryName = ClassName.getSimpleName(repositoryClassName);

            removeDefaultConstructor(subClass);
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
            CtClass superInterface = classPool.getCtClass(MotechDataService.class.getName());
            superInterface.setGenericSignature(getGenericSignature(typeName));

            CtClass newInterface = createOrRetrieveInterface(interfaceClassName, superInterface);

            // clear lookup methods before adding the new ones
            removeExistingMethods(newInterface);

            // for each lookup we generate three methods - normal lookup, lookup with query params and
            // a count method for the lookup
            for (Lookup lookup : entity.getLookups()) {
                for (LookupType lookupType : LookupType.values()) {
                    CtMethod lookupMethod = generateLookupInterface(entity, lookup, newInterface, lookupType);
                    newInterface.addMethod(lookupMethod);
                }
            }

            return newInterface.toBytecode();
        } catch (NotFoundException | IOException | CannotCompileException e) {
            throw new EntityInfrastructureException(e);
        }
    }

    private byte[] getServiceCode(String serviceClassName, String interfaceClassName,
                                  Entity entity) {
        try {
            CtClass superClass = classPool.getCtClass(DefaultMotechDataService.class.getName());
            superClass.setGenericSignature(getGenericSignature(entity.getClassName()));

            CtClass serviceInterface = classPool.getCtClass(interfaceClassName);

            CtClass serviceClass = createOrRetrieveClass(serviceClassName, superClass);

            // add the interface if its not already there
            if (!JavassistHelper.hasInterface(serviceClass, serviceInterface)) {
                serviceClass.addInterface(serviceInterface);
            }

            // clear lookup methods before adding the new ones
            removeExistingMethods(serviceClass);

            // for each lookup we generate three methods - normal lookup, lookup with query params and
            // a count method for the lookup
            for (Lookup lookup : entity.getLookups()) {
                for (LookupType lookupType : LookupType.values()) {
                    CtMethod lookupMethod = generateLookup(entity, lookup, serviceClass, lookupType);
                    serviceClass.addMethod(lookupMethod);
                }
            }

            return serviceClass.toBytecode();
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

    private CtMethod generateLookupInterface(Entity entity, Lookup lookup, CtClass interfaceClass, LookupType lookupType)
            throws CannotCompileException {
        final String className = entity.getClassName();
        final String lookupName = (lookupType == LookupType.COUNT) ?
                LookupName.lookupCountMethod(lookup.getLookupName()) :
                LookupName.lookupMethod(lookup.getLookupName());

        StringBuilder sb = new StringBuilder("");

        // appropriate return type
        sb.append(returnType(className, lookup, lookupType));
        sb.append(" ").append(lookupName).append("(");

        // construct the method signature using fields
        Iterator it = lookup.getFields().iterator();
        while (it.hasNext()) {
            Field field = (Field) it.next();
            sb.append(field.getType().getTypeClassName()).append(" ").append(field.getDisplayName());

            if (it.hasNext()) {
                sb.append(", ");
            }
        }

        // query params at the end for ordering/paging
        if (lookupType == LookupType.WITH_QUERY_PARAMS) {
            sb.append(queryParamsParam(lookup));
        }

        sb.append(");");

        CtMethod method = CtNewMethod.make(sb.toString(), interfaceClass);
        method.setGenericSignature(buildGenericSignature(entity, lookup));

        return method;
    }

    private CtMethod generateLookup(Entity entity, Lookup lookup, CtClass serviceClass, LookupType lookupType)
            throws CannotCompileException {
        final String className = entity.getClassName();
        final String lookupName = (lookupType == LookupType.COUNT) ?
                LookupName.lookupCountMethod(lookup.getLookupName()) :
                LookupName.lookupMethod(lookup.getLookupName());

        // main method builder
        StringBuilder sb = new StringBuilder("public ");
        // param names array
        StringBuilder paramsSb = new StringBuilder("new java.lang.String[] {");
        // param values array
        StringBuilder valuesSb = new StringBuilder("new java.lang.Object[] {");

        // appropriate return type
        sb.append(returnType(className, lookup, lookupType));
        sb.append(" ").append(lookupName).append("(");

        // fields are used to construct the method signature, param names and values
        Iterator it = lookup.getFields().iterator();
        while (it.hasNext()) {
            Field field = (Field) it.next();

            paramsSb.append("\"").append(field.getName()).append("\"");

            valuesSb.append(field.getDisplayName());

            sb.append(field.getType().getTypeClassName()).append(" ").append(field.getDisplayName());

            if (it.hasNext()) {
                sb.append(", ");
                paramsSb.append(", ");
                valuesSb.append(", ");
            }
        }

        // ordering and paging param comes last
        if (lookupType == LookupType.WITH_QUERY_PARAMS) {
            sb.append(queryParamsParam(lookup));
        }

        paramsSb.append("}");
        valuesSb.append("}");

        // we call retrieveAll() if there are no params
        String callStr = (lookup.getFields().isEmpty()) ? "" : paramsSb.toString() + ", " + valuesSb.toString();

        sb.append(") {");

        // method body
        sb.append(buildMethodBody(entity, lookup, lookupType, callStr));

        sb.append(";}");

        CtMethod method = CtNewMethod.make(sb.toString(), serviceClass);

        // count method doesn't need a generic signature
        if (lookupType != LookupType.COUNT) {
            method.setGenericSignature(buildGenericSignature(entity, lookup));
        }

        return method;
    }

    private String buildMethodBody(Entity entity, Lookup lookup, LookupType lookupType, String callStr) {
        StringBuilder sb = new StringBuilder();

        if (lookupType == LookupType.COUNT) {
            if (lookup.isSingleObjectReturn()) {
                // single object returns always return only 1
                sb.append("return 1L;");
            } else {
                // count from db
                sb.append("return count(").append(callStr).append(");");
            }
        } else {
            sb.append("java.util.List list = ");

            sb.append("retrieveAll(").append(callStr);

            if (lookupType == LookupType.WITH_QUERY_PARAMS) {
                // append comma only if there were any params to start with
                if (!lookup.getFields().isEmpty()) {
                    sb.append(", ");
                }
                sb.append("queryParams");
            }

            sb.append(");");

            if (lookup.isSingleObjectReturn()) {
                sb.append("return list.isEmpty() ? null : (").append(entity.getClassName()).append(") list.get(0)");
            } else {
                sb.append("return list");
            }
        }

        return sb.toString();
    }

    private String buildGenericSignature(Entity entity, Lookup lookup) {
        // we must build generic signatures for lookup methods
        // an example signature for the method signature
        // List<org.motechproject.mds.Test> method(String p1, Integer p2)
        // is
        // cmt -- (Ljava/lang/String;Ljava/lang/Integer;)Ljava/util/List<Lorg/motechproject/mds/Test;>;
        StringBuilder sb = new StringBuilder();

        sb.append('(');
        for (Field field : lookup.getFields()) {
            sb.append(JavassistHelper.toGenericParam(field.getType().getTypeClass()));
        }
        sb.append(')');

        if (lookup.isSingleObjectReturn()) {
            sb.append(JavassistHelper.toGenericParam(entity.getClassName()));
        } else {
            sb.append(JavassistHelper.genericSignature(List.class.getName(), entity.getClassName()));
        }

        return sb.toString();
    }

    private CtClass createOrRetrieveClass(String className, CtClass superClass) {
        return createOrRetrieveClassOrInterface(className, superClass, false);
    }

    private CtClass createOrRetrieveInterface(String className, CtClass superClass) {
        return createOrRetrieveClassOrInterface(className, superClass, true);
    }

    private CtClass createOrRetrieveClassOrInterface(String className, CtClass superClass, boolean isInterface) {
        // if class is already declared we defrost
        // otherwise we create a new one
        CtClass existing = classPool.getOrNull(className);
        if (existing != null) {
            existing.defrost();
            return existing;
        } else {
            if (isInterface) {
                return classPool.makeInterface(className, superClass);
            } else {
                return classPool.makeClass(className, superClass);
            }
        }
    }

    private void removeExistingMethods(CtClass ctClass) {
        // we remove methods declared in the given class(not inherited)
        // that way we clear all lookups
        CtMethod[] methods = ctClass.getMethods();
        for (CtMethod method : methods) {
            if (method.getDeclaringClass().equals(ctClass)) {
                try {
                    ctClass.removeMethod(method);
                } catch (NotFoundException e) {
                    LOG.error(String.format("Method %s in class %s not found", method.getName(), ctClass.getName()), e);
                }
            }
        }
    }

    private void removeDefaultConstructor(CtClass ctClass) {
        // remove the constructor with no args
        CtConstructor[] constructors = ctClass.getConstructors();
        for (CtConstructor constructor : constructors) {
            try {
                if (ArrayUtils.isEmpty(constructor.getParameterTypes())) {
                    ctClass.removeConstructor(constructor);
                }
            } catch (NotFoundException e) {
                LOG.error("Unable to remove constructor", e);
            }
        }
    }

    private String returnType(String className, Lookup lookup, LookupType lookupType) {
        if (lookupType == LookupType.COUNT) {
            return "long";
        } else if (lookup.isSingleObjectReturn()) {
            return className;
        } else {
            return List.class.getName();
        }
    }

    private String queryParamsParam(Lookup lookup) {
        StringBuilder sb = new StringBuilder();
        if (!lookup.getFields().isEmpty()) {
            sb.append(", ");
        }
        sb.append(QueryParams.class.getName()).append(" queryParams");
        return sb.toString();
    }

    /**
     * Represents the three lookup methods generated
     * 1) simple retrieving
     * 2) paged/ordered retrieving
     * 3) result count
     */
    private static enum LookupType {
        SIMPLE, WITH_QUERY_PARAMS, COUNT
    }
}
