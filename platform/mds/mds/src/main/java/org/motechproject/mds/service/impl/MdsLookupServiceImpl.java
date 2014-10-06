package org.motechproject.mds.service.impl;

import org.motechproject.mds.dto.DtoHelper;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.ex.CollectionResultFromLookupExpectedException;
import org.motechproject.mds.ex.SingleResultFromLookupExpectedException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.lookup.LookupExecutor;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.MDSLookupService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.service.ServiceUtil;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Implementation of the {@link org.motechproject.mds.service.MDSLookupService}.
 * This runs in the MDS context(not entities context). All calls are delegated to the
 * respective data service for the entity.
 */
@Service("mdsLookupServiceImpl")
public class MdsLookupServiceImpl implements MDSLookupService {

    @Autowired
    private BundleContext bundleContext;

    @Autowired
    private EntityService entityService;

    @Override
    public <T> T findOne(Class<T> entityClass, String lookupName, Map<String, ?> lookupParams) {
        return findOne(entityClass.getName(), lookupName, lookupParams);
    }

    @Override
    public <T> T findOne(String entityClassName, String lookupName, Map<String, ?> lookupParams) {
        LookupExecutor lookupExecutor = buildLookupExecutor(entityClassName, lookupName);

        Object result = lookupExecutor.execute(lookupParams);

        return assertAndReturnSingleResult(result, lookupName);
    }

    @Override
    public <T> List<T> findMany(Class<T> entityClass, String lookupName, Map<String, ?> lookupParams) {
        return findMany(entityClass.getName(), lookupName, lookupParams, null);
    }

    @Override
    public <T> List<T> findMany(String entityClassName, String lookupName, Map<String, ?> lookupParams) {
        return findMany(entityClassName, lookupName, lookupParams, null);
    }

    @Override
    public <T> List<T> findMany(Class<T> entityClass, String lookupName, Map<String, ?> lookupParams, QueryParams queryParams) {
        return findMany(entityClass.getName(), lookupName, lookupParams, queryParams);
    }

    @Override
    public <T> List<T> findMany(String entityClassName, String lookupName, Map<String, ?> lookupParams, QueryParams queryParams) {
        LookupExecutor lookupExecutor = buildLookupExecutor(entityClassName, lookupName);

        Object result = lookupExecutor.execute(lookupParams, queryParams);

        return assertAndReturnListResult(result, lookupName);
    }

    @Override
    public <T> List<T> retrieveAll(Class<T> entityClass) {
        return retrieveAll(entityClass.getName(), null);
    }

    @Override
    public <T> List<T> retrieveAll(String entityClassName) {
        return retrieveAll(entityClassName, null);
    }

    @Override
    public <T> List<T> retrieveAll(Class<T> entityClass, QueryParams queryParams) {
        return retrieveAll(entityClass.getName(), queryParams);
    }

    @Override
    public <T> List<T> retrieveAll(String entityClassName, QueryParams queryParams) {
        MotechDataService<T> dataService = ServiceUtil.getServiceForInterfaceName(bundleContext,
                MotechClassPool.getInterfaceName(entityClassName));

        return dataService.retrieveAll(queryParams);
    }

    @Override
    public long count(Class entityClass, String lookupName, Map<String, ?> lookupParams) {
        return count(entityClass.getName(), lookupName, lookupParams);
    }

    @Override
    public long count(String entityClassName, String lookupName, Map<String, ?> lookupParams) {
        LookupExecutor lookupExecutor = buildLookupExecutor(entityClassName, lookupName);

        return lookupExecutor.executeCount(lookupParams);
    }

    @Override
    public long countAll(Class entityClass) {
        return countAll(entityClass.getName());
    }

    @Override
    public long countAll(String entityClassName) {
        MotechDataService dataService = ServiceUtil.getServiceForInterfaceName(bundleContext,
                MotechClassPool.getInterfaceName(entityClassName));

        return dataService.count();
    }

    private LookupExecutor buildLookupExecutor(String entityClassName, String lookupName) {
        String fullyQualifiedEntityClassName;
        if (entityClassName.contains(".")) {
            fullyQualifiedEntityClassName = entityClassName;
        } else {
            fullyQualifiedEntityClassName = Constants.PackagesGenerated.ENTITY + "." + entityClassName;
        }
        MotechDataService dataService = ServiceUtil.getServiceForInterfaceName(bundleContext,
                MotechClassPool.getInterfaceName(fullyQualifiedEntityClassName));

        EntityDto entity = entityService.getEntityByClassName(fullyQualifiedEntityClassName);
        LookupDto lookup = entityService.getLookupByName(entity.getId(), lookupName);
        List<FieldDto> fields = entityService.getEntityFields(entity.getId());

        return new LookupExecutor(dataService, lookup, DtoHelper.asFieldMapById(fields));
    }

    private <T> T assertAndReturnSingleResult(Object result, String lookupName) {
        if (result instanceof Collection) {
            throw new SingleResultFromLookupExpectedException(lookupName);
        }
        return (T) result;
    }

    private <T> List<T> assertAndReturnListResult(Object result, String lookupName) {
        if (result != null && !(result instanceof Collection)) {
            throw new CollectionResultFromLookupExpectedException(lookupName);
        }
        return (List<T>) result;
    }
}
