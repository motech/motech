package org.motechproject.mds;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.motechproject.commons.api.AbstractDataProvider;
import org.motechproject.commons.api.DataProvider;
import org.motechproject.mds.builder.MDSDataProviderBuilder;
import org.motechproject.mds.dto.DtoHelper;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.lookup.LookupExecutor;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;
import org.motechproject.osgi.web.util.OSGiServiceUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Class responsible for all actions connected with registering MDS data provider in Task module.
 */
@Component("mdsDataProvider")
public class MDSDataProvider extends AbstractDataProvider {

    private ResourceLoader resourceLoader;
    private MDSDataProviderBuilder mdsDataProviderBuilder;
    private BundleContext bundleContext;
    private ServiceRegistration serviceRegistration;
    private EntityService entityService;

    @Autowired
    public MDSDataProvider(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        registerDataProvider();
    }

    @Override
    public String getName() {
        return "data-services";
    }

    @Override
    public Object lookup(String type, String lookupName, Map<String, String> lookupMap) {
        Object obj = null;

        LookupDto lookup = null;
        EntityDto entity = entityService.getEntityByClassName(type);
        if (entity != null) {
            lookup = entityService.getLookupByName(entity.getId(), lookupName);
        }

        if (entity != null && lookup != null) {
            String serviceName = MotechClassPool.getInterfaceName(type);
            MotechDataService service = OSGiServiceUtils.findService(bundleContext, serviceName);

            if (service != null) {
                List<FieldDto> fields = entityService.getEntityFields(entity.getId());
                Map<String, FieldDto> fieldsByName = DtoHelper.asFieldMapByName(fields);

                LookupExecutor executor = new LookupExecutor(service, lookup, fieldsByName);

                obj = executor.execute(lookupMap);
            } else {
                logError("Service %s not found", serviceName);
            }
        }

        // we allow executing lookups that return multiple objects
        // if such a lookup returns more then 1 object we throw an exception
        Object result = null;
        if (obj instanceof Collection) {
            Collection collection = (Collection) obj;
            if (collection.size() == 1) {
                result = collection.iterator().next();
            } else if (collection.size() > 1) {
                throw new IllegalArgumentException(
                        String.format("Data provided lookup for %s returned more then 1 object, number of objects found: %d",
                                type, collection.size()));
            }
        } else {
            result = obj;
        }

        return result;
    }

    @Override
    protected Class<?> getClassForType(String type) throws ClassNotFoundException {
        Bundle entitiesBundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext,
                Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME);
        return (entitiesBundle != null) ? entitiesBundle.loadClass(type) : null;
    }

    @Override
    public List<Class<?>> getSupportClasses() {
        List<EntityDto> dtos = entityService.listEntities();
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
    public boolean supports(String type) {
        return entityService.getEntityByClassName(type) != null;
    }

    @Override
    public String getPackageRoot() {
        return "org.motechproject.mds.entity";
    }

    public void updateDataProvider() {
        setBody(mdsDataProviderBuilder.generateDataProvider());
        // we unregister the service, then register again
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
            serviceRegistration = null;
        }
        // only register if we actually have entities
        if (CollectionUtils.isNotEmpty(entityService.getEntitiesWithLookups())) {
            serviceRegistration = bundleContext.registerService(DataProvider.class.getName(), this, null);
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
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }
}
