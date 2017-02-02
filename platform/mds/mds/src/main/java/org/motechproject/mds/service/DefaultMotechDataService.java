package org.motechproject.mds.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.entityinfo.EntityInfo;
import org.motechproject.mds.entityinfo.EntityInfoReader;
import org.motechproject.mds.entityinfo.FieldInfo;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.exception.audit.HistoryInstanceNotFoundException;
import org.motechproject.mds.exception.object.SchemaVersionException;
import org.motechproject.mds.exception.audit.TrashInstanceNotFoundException;
import org.motechproject.mds.exception.object.ObjectNotFoundException;
import org.motechproject.mds.exception.object.ObjectUpdateException;
import org.motechproject.mds.exception.object.SecurityException;
import org.motechproject.mds.filter.Filters;
import org.motechproject.mds.query.Property;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.query.SqlQueryExecution;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.mds.util.StateManagerUtil;
import org.motechproject.server.osgi.event.OsgiEventProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jdo.JdoTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.jdo.JDOHelper;
import javax.jdo.ObjectState;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.motechproject.mds.event.CrudEventBuilder.buildEventParams;
import static org.motechproject.mds.event.CrudEventBuilder.createSubject;
import static org.motechproject.mds.event.CrudEventType.CREATE;
import static org.motechproject.mds.event.CrudEventType.DELETE;
import static org.motechproject.mds.event.CrudEventType.UPDATE;
import static org.motechproject.mds.util.Constants.Util.CREATOR_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFICATION_DATE_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFIED_BY_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.OWNER_FIELD_NAME;
import static org.motechproject.mds.util.PropertyUtil.safeSetProperty;
import static org.motechproject.mds.util.SecurityUtil.getUsername;

/**
 * This is a basic implementation of {@link org.motechproject.mds.service.MotechDataService}. Mainly
 * it is used as super class to create a service related with the given entity schema in
 * {@link org.motechproject.mds.builder.EntityInfrastructureBuilder} but it can be also used by
 * other services inside this package.
 *
 * @param <T> the type of entity schema.
 */
@Service
public abstract class DefaultMotechDataService<T> implements MotechDataService<T> {

    private static final Logger MDS_LOGGER = LoggerFactory.getLogger(DefaultMotechDataService.class);

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private MotechDataRepository<T> repository;
    private TrashService trashService;
    private HistoryService historyService;
    private OsgiEventProxy osgiEventProxy;
    private JdoTransactionManager transactionManager;
    private ApplicationContext applicationContext;
    private EntityInfoReader entityInfoReader;

    private SecurityMode securityMode;
    private Long schemaVersion;
    private boolean recordHistory;
    private boolean allowCreateEvent;
    private boolean allowUpdateEvent;
    private boolean allowDeleteEvent;
    private String module;
    private String entityName;
    private String namespace;
    private String versionFieldName;

    @PostConstruct
    public void init() {
        debug("Initializing {}", getClass().getName());

        EntityInfo entityInfo = entityInfoReader.getEntityInfo(repository.getClassType().getName());

        securityMode = entityInfo.getSecurityMode();
        schemaVersion = entityInfo.getSchemaVersion();
        recordHistory = entityInfo.isRecordHistory();
        allowCreateEvent = entityInfo.isCreateEventFired();
        allowUpdateEvent = entityInfo.isUpdateEventFired();
        allowDeleteEvent = entityInfo.isDeleteEventFired();
        module = entityInfo.getModule();
        entityName = entityInfo.getEntityName();
        namespace = entityInfo.getNamespace();

        // we need the field types for handling lookups with null values
        Map<String, String> fieldTypeMap = new HashMap<>();
        for (FieldInfo field : entityInfo.getFieldsInfo()) {
            fieldTypeMap.put(field.getName(), field.getType());
            if (field.isVersionField()) {
                versionFieldName = field.getName();
            }
        }

        repository.setFieldTypeMap(fieldTypeMap);

        debug("{} ready", getClass().getName());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public T create(final T object) {
        validateCredentials();

        final T createdInstance = repository.create(object);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                if (allowCreateEvent) {
                    sendEvent((Long) getId(createdInstance), CREATE);
                }
            }
        });

        return createdInstance;
    }

    @Override
    @Transactional
    public T retrieve(String primaryKeyName, Object value) {
        T instance = repository.retrieve(primaryKeyName, value);
        validateCredentials(instance);

        return instance;
    }

    @Override
    @Transactional
    public List<T> retrieveAll() {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.retrieveAll(QueryParams.ORDER_ID_ASC, securityRestriction);
    }

    @Override
    @Transactional
    public List<T> retrieveAll(QueryParams queryParams) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.retrieveAll(queryParams, securityRestriction);
    }

    @Override
    @Transactional
    public T update(final T object) {
        if (JDOHelper.getObjectState(object) == ObjectState.TRANSIENT) {
            return updateFromTransient(object);
        } else {
            validateCredentials(object);

            updateModificationData(object);
            final T updatedInstance = repository.update(object);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    if (allowUpdateEvent) {
                        sendEvent((Long) getId(updatedInstance), UPDATE);
                    }
                }
            });

            return updatedInstance;
        }
    }

    @Override
    @Transactional
    public T createOrUpdate (final T object) {
        final T result;

        if (getId(object) == null) {
            result = create(object);
        } else {
            result = update(object);
        }
        return result;
    }

    @Override
    @Transactional
    public T updateFromTransient(T transientObject) {
        return updateFromTransient(transientObject, null);
    }

    @Override
    @Transactional
    public T updateFromTransient(final T transientObject, final Set<String> fieldsToUpdate) {
        validateCredentials(transientObject);

        final T fromDbInstance = findById((Long) getId(transientObject));
        final T result;

        if (fromDbInstance == null) {
            // create will send the CRUD event
            result = create(transientObject);
        } else {
            PropertyUtil.copyProperties(fromDbInstance, transientObject, null, fieldsToUpdate);

            if (versionFieldName != null) {
                StateManagerUtil.setTransactionVersion(fromDbInstance, versionFieldName);
            }

            updateModificationData(fromDbInstance);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    if (allowUpdateEvent) {
                        sendEvent((Long) getId(fromDbInstance), UPDATE);
                    }
                }
            });
            result = fromDbInstance;
        }

        return result;
    }

    private void updateModificationData(Object obj) {
        safeSetProperty(obj, MODIFICATION_DATE_FIELD_NAME, DateUtil.now());
        safeSetProperty(obj, MODIFIED_BY_FIELD_NAME, defaultIfBlank(getUsername(), ""));
    }

    @Override
    @Transactional
    public void delete(final T object) {
        if (object == null) {
            throw new IllegalArgumentException("Unable to delete null object");
        }

        validateCredentials(object);

        Long deletedInstanceId = doInTransaction(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus status) {
                // independent of trash mode remove object. If trash mode is active then the same object
                // exists in the trash so this one is unnecessary.
                // We retrieve the object using the current pm
                Long id = (Long) getId(object);
                T existing = findById(id);

                repository.delete(existing);
                return id;
            }
        });

        if (allowDeleteEvent) {
            sendEvent(deletedInstanceId, DELETE);
        }
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        delete(Constants.Util.ID_FIELD_NAME, id);
    }

    @Override
    @Transactional
    public void delete(String primaryKeyName, Object value) {
        T instance = retrieve(primaryKeyName, value);
        if (instance != null) {
            delete(instance);
        } else {
            logger.warn("Attempted to delete non-existing object with {}={}", primaryKeyName, value);
        }
    }

    @Override
    @Transactional
    public Object findTrashInstanceById(Long trashId) {
        return trashService.findTrashById(trashId, getClassType().getName());
    }

    @Override
    @Transactional
    public T revertFromTrash(Long trashId) {
        validateCredentials();

        Object trashRecord = trashService.findTrashById(trashId, getClassType().getName());
        if (trashRecord == null) {
            throw new TrashInstanceNotFoundException(getClassType().getName(), trashId);
        }
        verifySchemaVersion(trashRecord, trashId, true);

        try {
            T newInstance = getClassType().newInstance();

            copyValuesFromRecord(newInstance, trashRecord);
            newInstance = create(newInstance);

            trashService.removeFromTrash(trashRecord);

            return newInstance;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ObjectUpdateException(trashRecord.getClass().getName(), trashId, e);
        }
    }

    @Override
    @Transactional
    public T revertToHistoricalRevision(Long instanceId, Long historicalId) {
        validateCredentials();

        T instance = findById(instanceId);
        if (instance == null) {
            throw new ObjectNotFoundException(EntityType.HISTORY.getClassName(getClassType().getName()), historicalId);
        }

        Object historyRecord = historyService.getSingleHistoryInstance(instance, historicalId);
        if (historyRecord == null) {
            throw new HistoryInstanceNotFoundException(getClassType().getName(), instanceId, historicalId);
        }
        verifySchemaVersion(historyRecord, historicalId, false);

        copyValuesFromRecord(instance, historyRecord);

        return update(instance);
    }

    @Override
    @Transactional
    public long count() {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.count(securityRestriction);
    }

    @Override
    @Transactional
    public T detachedCopy(T object) {
        if (JDOHelper.getObjectState(object) == ObjectState.TRANSIENT) {
            return repository.detachedCopy(findById((Long) getId(object)));
        }
        return object;
    }

    @Override
    @Transactional
    public List<T> detachedCopyAll(List<T> objects) {
        List<T> detachedCopies = new ArrayList<>();

        for (T object : objects) {
            detachedCopies.add(detachedCopy(object));
        }

        return detachedCopies;
    }

    @Override
    @Transactional
    public Object getDetachedField(T instance, String fieldName) {
        if (JDOHelper.getObjectState(instance) == ObjectState.TRANSIENT) {
            return repository.getDetachedField(findById((Long) getId(instance)), fieldName);
        }
        return repository.getDetachedField(instance, fieldName);
    }

    @Override
    @Transactional
    public List<T> filter(Filters filters, QueryParams queryParams) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.filter(filters, queryParams, securityRestriction);
    }

    @Override
    @Transactional
    public long countForFilters(Filters filters) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.countForFilters(filters, securityRestriction);
    }

    @Override
    @Transactional
    public void deleteAll() {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        repository.delete(new String[0], new Object[0], securityRestriction);
    }

    @Override
    @Transactional
    public <R> R executeQuery(QueryExecution<R> queryExecution) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        Query query = repository.getPersistenceManager().newQuery(repository.getClassType());
        return queryExecution.execute(query, securityRestriction);
    }

    @Override
    public Class<T> getClassType() {
        return repository.getClassType();
    }

    @Override
    public String getVersionFieldName(){
        return versionFieldName;
    }

    @Override
    public void evictAllCache() {
        repository.evictAll();
    }

    @Override
    public void evictCacheForInstance(T instance) {
        repository.evictOne(instance);
    }

    @Override
    public void evictEntityCache(boolean withSubclasses) {
        repository.evictEntity(withSubclasses);
    }

    @Override
    @Transactional
    public T findById(Long id) {
        if (id == null) {
            return null;
        }
        return retrieve(Constants.Util.ID_FIELD_NAME, id);
    }

    @Override
    @Transactional
    public List<T> findByIds(Collection<Long> ids) {
        if (ids == null) {
            return new ArrayList<>();
        }

        return repository.retrieveAll(ids);
    }

    @Override
    public <R> R doInTransaction(TransactionCallback<R> transactionCallback) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return transactionTemplate.execute(transactionCallback);
    }

    @Transactional
    public <R> R executeSQLQuery(SqlQueryExecution<R> queryExecution) {
        Query query = repository.getPersistenceManager().
                newQuery(Constants.Util.SQL_QUERY, queryExecution.getSqlQuery());
        return queryExecution.execute(query);
    }

    @Override
    public Long getSchemaVersion() {
        return schemaVersion;
    }

    @Override
    public boolean recordHistory() {
        return recordHistory;
    }

    protected void copyValuesFromRecord(T target, Object record) {
        EntityInfo entityInfo = entityInfoReader.getEntityInfo(getClassType().getName());
        RevertConverter revertConverter = new RevertConverter(entityInfo, applicationContext);

        PropertyUtil.copyProperties(target, record, revertConverter);
        PropertyUtil.copyProperties(target, record, revertConverter, Constants.Util.RECORD_FIELDS_TO_COPY);
    }

    protected List<T> retrieveAll(List<Property> properties) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.retrieveAll(properties, securityRestriction);
    }

    protected List<T> retrieveAll(List<Property> properties, QueryParams queryParams) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.retrieveAll(properties, queryParams, securityRestriction);
    }

    protected long count(List<Property> properties) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.count(properties, securityRestriction);
    }

    protected InstanceSecurityRestriction validateCredentials() {
        return validateCredentials(null);
    }

    protected InstanceSecurityRestriction validateCredentials(T instance) {
        InstanceSecurityRestriction restriction = new InstanceSecurityRestriction();
        restriction.setByOwner(securityMode == SecurityMode.OWNER);
        restriction.setByCreator(securityMode == SecurityMode.CREATOR);

        if (!restriction.isEmpty() && instance != null) {
            restriction = checkInstanceAccess(instance, restriction);
        }

        return restriction;
    }

    protected void verifySchemaVersion(Object record, Long recordId, boolean forTrash) {
        String schemaField;
        if (forTrash) {
            schemaField = Constants.Util.SCHEMA_VERSION_FIELD_NAME;
        } else {
            schemaField = HistoryTrashClassHelper.historySchemaVersion(record.getClass());
        }
        Long recordSchemaVersion = (Long) PropertyUtil.safeGetProperty(record, schemaField);
        if (!schemaVersion.equals(recordSchemaVersion)) {
            throw new SchemaVersionException(schemaVersion, recordSchemaVersion, recordId, record.getClass().getName());
        }
    }

    private InstanceSecurityRestriction checkInstanceAccess(T instance, InstanceSecurityRestriction restriction) {
        T fromDb = repository.retrieve(getId(instance));

        String creator = (String) PropertyUtil.safeGetProperty(fromDb, CREATOR_FIELD_NAME);
        String owner = (String) PropertyUtil.safeGetProperty(fromDb, OWNER_FIELD_NAME);

        String username = getUsername();

        boolean authorized = false;

        if (restriction.isByOwner()) {
            authorized = StringUtils.equals(username, owner);
        } else if (restriction.isByCreator()) {
            authorized = StringUtils.equals(username, creator);
        }

        if (!authorized) {
            throw new SecurityException();
        }

        return restriction;
    }

    private void sendEvent(Long id, CrudEventType action) {
        String subject = createSubject(module, namespace, entityName, action);
        Map<String, Object> params = buildEventParams(module, namespace, entityName, getClassType().getName(), id);
        osgiEventProxy.sendEvent(subject, params);
    }

    protected Object getId(T instance) {
        return PropertyUtil.safeGetProperty(instance, Constants.Util.ID_FIELD_NAME);
    }

    protected Logger getLogger() {
        return logger;
    }

    protected void debug(String msg, Object... args) {
        if (MDS_LOGGER.isDebugEnabled() && !getLogger().isDebugEnabled()) {
            MDS_LOGGER.debug(msg, args);
        } else {
            getLogger().debug(msg, args);
        }
    }

    protected MotechDataRepository<T> getRepository() {
        return repository;
    }

    public void setRepository(MotechDataRepository<T> repository) {
        this.repository = repository;
    }

    @Autowired
    public void setTrashService(TrashService trashService) {
        this.trashService = trashService;
    }

    @Autowired
    public void setOsgiEventProxy(OsgiEventProxy osgiEventProxy) {
        this.osgiEventProxy = osgiEventProxy;
    }

    @Autowired
    @Qualifier("transactionManager")
    public void setTransactionManager(JdoTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    @Autowired
    public void setEntityInfoReader(EntityInfoReader entityInfoReader) {
        this.entityInfoReader = entityInfoReader;
    }
}
