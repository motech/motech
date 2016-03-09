package org.motechproject.mds.builder.impl;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import org.apache.commons.lang.ArrayUtils;
import org.motechproject.mds.builder.EntityInfrastructureBuilder;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.exception.entity.EntityInfrastructureException;
import org.motechproject.mds.helper.MdsBundleHelper;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.TransactionalMotechDataService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.JavassistUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityInfrastructureBuilderImpl.class);

    private final ClassPool classPool = MotechClassPool.getDefault();

    private BundleContext bundleContext;

    @Override
    public List<ClassData> buildInfrastructure(EntityDto entity, SchemaHolder schemaHolder) {
        return build(entity.getClassName(), entity, schemaHolder);
    }

    @Override
    public List<ClassData> buildHistoryInfrastructure(String className) {
        return build(className, null, null);
    }

    private List<ClassData> build(String className, EntityDto entity, SchemaHolder schemaHolder) {
        List<ClassData> list = new ArrayList<>();

        // create a repository(dao) for the entity
        String repositoryClassName = MotechClassPool.getRepositoryName(className);
        byte[] repositoryCode = getRepositoryCode(repositoryClassName, className, entity.getMaxFetchDepth());
        list.add(new ClassData(repositoryClassName, repositoryCode));

        // create an interface for the service
        String interfaceClassName = MotechClassPool.getInterfaceName(className);
        byte[] interfaceCode = getInterfaceCode(interfaceClassName, className, entity, schemaHolder);
        list.add(new ClassData(interfaceClassName, entity.getModule(), entity.getNamespace(), interfaceCode, true));

        // create the implementation of the service
        String serviceClassName = MotechClassPool.getServiceImplName(className);
        byte[] serviceCode = getServiceCode(
                serviceClassName, interfaceClassName, className, entity, schemaHolder
        );
        list.add(new ClassData(serviceClassName, serviceCode));

        return list;
    }

    private byte[] getRepositoryCode(String repositoryClassName, String typeName, Integer fetchDepth) {
        try {
            CtClass superClass = classPool.getCtClass(MotechDataRepository.class.getName());
            superClass.setGenericSignature(getGenericSignature(typeName));

            CtClass subClass = createOrRetrieveClass(repositoryClassName, superClass);

            String repositoryName = ClassName.getSimpleName(repositoryClassName);

            removeDefaultConstructor(subClass);
            String constructorAsString;

            // the fetch depth parameter is optional
            if (fetchDepth == null) {
                constructorAsString = String.format(
                        "public %s(){super(%s.class);}", repositoryName, typeName
                );
            } else {
                constructorAsString = String.format(
                        "public %s(){super(%s.class, %d);}", repositoryName, typeName, fetchDepth
                );
            }


            CtConstructor constructor = CtNewConstructor.make(constructorAsString, subClass);

            subClass.addConstructor(constructor);

            return subClass.toBytecode();
        } catch (NotFoundException | CannotCompileException | IOException e) {
            throw new EntityInfrastructureException(repositoryClassName, e);
        }
    }

    private byte[] getInterfaceCode(String interfaceClassName, String className, EntityDto entity,
                                    SchemaHolder schemaHolder) {
        try {
            // the interface can come from the developer for DDE, but it doesn't have to
            // in which case it will be generated from scratch
            CtClass superInterface = null;

            if (null != entity && MotechClassPool.isServiceInterfaceRegistered(className)) {
                String ddeInterfaceName = MotechClassPool.getInterfaceName(className);
                Bundle declaringBundle = MdsBundleHelper.searchForBundle(bundleContext, entity);
                if (declaringBundle == null) {
                    LOGGER.error("Unable to find bundle declaring the DDE interface for {}", className);
                } else {
                    superInterface = JavassistUtil.loadClass(declaringBundle, ddeInterfaceName, classPool);
                }
            }

            // standard super interface - MotechDataService, for EUDE or DDE without an interface
            if (superInterface == null) {
                superInterface = classPool.getCtClass(MotechDataService.class.getName());
                superInterface.setGenericSignature(getGenericSignature(className));
            }

            CtClass interfaceClass = createOrRetrieveInterface(interfaceClassName, superInterface);

            List<CtMethod> methods = new ArrayList<>();

            // for each lookup we generate three methods - normal lookup, lookup with query params and
            // a count method for the lookup
            if (null != entity) {
                List<LookupDto> lookups = schemaHolder.getLookups(entity);

                for (LookupDto lookup : lookups) {
                    for (LookupType lookupType : LookupType.values()) {
                        LookupBuilder lookupBuilder = new LookupBuilder(entity, lookup, interfaceClass,
                                lookupType, schemaHolder);
                        methods.add(lookupBuilder.buildSignature());
                    }
                }
            }

            // clear lookup methods before adding the new ones
            removeExistingMethods(interfaceClass);

            for (CtMethod method : methods) {
                interfaceClass.addMethod(method);
            }

            return interfaceClass.toBytecode();
        } catch (NotFoundException | IOException | CannotCompileException e) {
            throw new EntityInfrastructureException(interfaceClassName, e);
        }
    }

    private byte[] getServiceCode(String serviceClassName, String interfaceClassName,
                                  String className, EntityDto entity, SchemaHolder schemaHolder) {
        try {
            CtClass superClass = classPool.getCtClass(TransactionalMotechDataService.class.getName());
            superClass.setGenericSignature(getGenericSignature(className));

            CtClass serviceInterface = classPool.getCtClass(interfaceClassName);

            CtClass serviceClass = createOrRetrieveClass(serviceClassName, superClass);

            // add the interface if its not already there
            if (!JavassistUtil.hasInterface(serviceClass, serviceInterface)) {
                serviceClass.addInterface(serviceInterface);
            }

            List<CtMethod> methods = new ArrayList<>();

            // for each lookup we generate three methods - normal lookup, lookup with query params and
            // a count method for the lookup
            if (null != entity) {
                List<LookupDto> lookups = schemaHolder.getLookups(entity);

                for (LookupDto lookup : lookups) {
                    for (LookupType lookupType : LookupType.values()) {
                        LookupBuilder lookupBuilder = new LookupBuilder(entity, lookup, serviceClass,
                                lookupType, schemaHolder);
                        methods.add(lookupBuilder.buildMethod());
                    }
                }
            }

            // clear lookup methods before adding the new ones
            removeExistingMethods(serviceClass);

            for (CtMethod method : methods) {
                serviceClass.addMethod(method);
            }

            return serviceClass.toBytecode();
        } catch (NotFoundException | IOException | CannotCompileException e) {
            throw new EntityInfrastructureException(serviceClassName, e);
        }
    }

    private static String getGenericSignature(String typeName) {
        ClassType classType = new ClassType(typeName);
        TypeParameter parameter = new TypeParameter("T", classType, null);
        ClassSignature sig = new ClassSignature(new TypeParameter[]{parameter});

        return sig.encode();
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
        CtMethod[] methods = ctClass.getDeclaredMethods();
        if (ArrayUtils.isNotEmpty(methods)) {
            for (CtMethod method : methods) {
                try {
                    ctClass.removeMethod(method);
                } catch (NotFoundException e) {
                    LOGGER.error(String.format("Method %s in class %s not found", method.getName(), ctClass.getName()), e);
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
                LOGGER.error("Unable to remove constructor", e);
            }
        }
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
