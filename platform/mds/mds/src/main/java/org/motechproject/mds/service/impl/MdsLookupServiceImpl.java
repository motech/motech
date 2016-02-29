package org.motechproject.mds.service.impl;

import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.exception.lookup.SingleResultFromLookupExpectedException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.lookup.LookupExecutor;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.MDSLookupService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;
import org.motechproject.osgi.web.util.OSGiServiceUtils;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
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

        return returnListResult(result);
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
        MotechDataService<T> dataService = OSGiServiceUtils.findService(bundleContext,
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
        MotechDataService dataService = OSGiServiceUtils.findService(bundleContext,
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
        MotechDataService dataService = OSGiServiceUtils.findService(bundleContext,
                MotechClassPool.getInterfaceName(fullyQualifiedEntityClassName));

        EntityDto entity = entityService.getEntityByClassName(fullyQualifiedEntityClassName);
        LookupDto lookup = entityService.getLookupByName(entity.getId(), lookupName);

        return new LookupExecutor(dataService, lookup, entityService.getLookupFieldsMapping(entity.getId(), lookupName));
    }

    private <T> T assertAndReturnSingleResult(Object result, String lookupName) {
        if (result instanceof Collection) {
            throw new SingleResultFromLookupExpectedException(lookupName);
        }
        return (T) result;
    }

    private <T> List<T> returnListResult(Object result) {
        if (result != null && !(result instanceof Collection)) {
            return (List<T>) Collections.singletonList(result);
        }
        return (List<T>) result;
    }
}
