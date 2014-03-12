package org.motechproject.mds;

import org.apache.commons.beanutils.MethodUtils;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.api.AbstractDataProvider;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.builder.MDSDataProviderBuilder;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Class responsible for all actions connected with registering MDS data provider in Task module.
 */

@Component("mdsDataProvider")
public class MDSDataProvider extends AbstractDataProvider {

    private ResourceLoader resourceLoader;
    private MDSDataProviderBuilder mdsDataProviderBuilder;
    private BundleContext bundleContext;
    private TaskDataProviderService taskDataProviderService;

    @Autowired
    public MDSDataProvider(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        registerDataProvider();
    }

    @Override
    public String getName() {
        return "MDS";
    }

    @Override
    public Object lookup(String type, String lookupName, Map<String, String> lookupFields) {
        Object obj = null;
        String className = type.substring(type.lastIndexOf('.') + 1).trim();

        if (supports(className)) {
            String serviceName = ClassName.getInterfaceName(getPackageRoot() + "." + className);
            ServiceReference ref = bundleContext.getServiceReference(serviceName);
            if (ref != null) {
                try {
                    Class<?> objectClass =  OsgiBundleUtils.findBundleBySymbolicName(bundleContext,
                            "org.motechproject.motech-dataservices-entities").loadClass(serviceName);

                    MotechDataService service = (MotechDataService) bundleContext.getService(ref);

                    obj = MethodUtils.invokeExactMethod(objectClass.cast(service), lookupName,
                            generateArgumentsForLookup(lookupFields, type));
                } catch (ClassNotFoundException e) {
                    logError("Class %s not found", serviceName, e);
                } catch (InvocationTargetException e) {
                    logError("Can't invoke method %s", lookupName, e);
                } catch (NoSuchMethodException e) {
                    logError("Method %s not found", lookupName, e);
                } catch (IllegalAccessException e) {
                    logError("Can't access method %s", lookupName, e);
                }
            } else {
                logError("Service %s not found", serviceName);
            }
        }
        return obj;
    }

    private Object[] generateArgumentsForLookup(Map<String, String> lookupFields, String type) throws ClassNotFoundException {
        EntityDto entityDto = mdsDataProviderBuilder.getEntityService().getEntityByClassName(type);
        List<FieldDto> fieldDtos = mdsDataProviderBuilder.getEntityService().getEntityFields(entityDto.getId());
        List<Object> args = new LinkedList<>();
        for (FieldDto dto : fieldDtos) {
            if (lookupFields.get(dto.getBasic().getName()) != null) {
                args.add(castToClass(dto.getType().getTypeClass(), lookupFields.get(dto.getBasic().getName())));
            }
        }
        Collections.reverse(args);
        return args.toArray();
    }

    private Object castToClass(String paramClass, String param) {
        Object castedParam = param;
        if (paramClass.compareTo(Long.class.getName()) == 0) {
            castedParam = Long.valueOf(param);
        } else if (paramClass.compareTo(Integer.class.getName()) == 0) {
            castedParam = Integer.valueOf(param);
        } else if (paramClass.compareTo(Double.class.getName()) == 0) {
            castedParam = Double.valueOf(param);
        } else if (paramClass.compareTo(Boolean.class.getName()) == 0) {
            castedParam = Boolean.valueOf(param);
        } else if (paramClass.compareTo(List.class.getName()) == 0) {
            castedParam = asList(param);
        } else if (paramClass.compareTo(Time.class.getName()) == 0) {
            castedParam = Time.valueOf(param);
        } else if (paramClass.compareTo(DateTime.class.getName()) == 0) {
            castedParam = DateTime.parse(param);
        } else if (paramClass.compareTo(Date.class.getName()) == 0) {
            castedParam = Date.parse(param);
        }
        return castedParam;
    }

    @Override
    protected Class<?> getClassForType(String type) throws ClassNotFoundException {
        Bundle entitiesBundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, "org.motechproject.motech-dataservices-entities");
        return (entitiesBundle != null) ? entitiesBundle.loadClass(String.format("%s.%s", getPackageRoot(), type)) : null;
    }

    @Override
    public List<Class<?>> getSupportClasses() {
        List<EntityDto> dtos = mdsDataProviderBuilder.getEntityService().listEntities();
        List<Class<?>> classes = new ArrayList<>();

        for (EntityDto dto : dtos) {
            try {
                classes.add(getClassForType(dto.getName()));
            } catch (ClassNotFoundException e) {
                logError(e.getMessage(), e);
            }
        }

        return classes;
    }

    @Override
    public String getPackageRoot() {
        return "org.motechproject.mds.entity";
    }

    public void updateDataProvider() {
            setBody(mdsDataProviderBuilder.generateDataProvider());
            if (getBody().length() != 0 && taskDataProviderService != null)  {
                taskDataProviderService.registerProvider(getBody());
            }
    }

    private void registerDataProvider() {
        Resource resource = resourceLoader.getResource("task-data-provider.json");
        if (resource != null) {
            setBody(resource);
        }
    }

    @Autowired
    public void setMdsDataProviderBuilder(MDSDataProviderBuilder mdsDataProviderBuilder) {
        this.mdsDataProviderBuilder = mdsDataProviderBuilder;
        updateDataProvider();
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Autowired
    public void setTaskDataProviderService(TaskDataProviderService taskDataProviderService) {
        this.taskDataProviderService = taskDataProviderService;
    }
}
