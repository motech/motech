package org.motechproject.mds.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.ex.SecurityException;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.QueryParams;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.security.domain.MotechUserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String ID = "id";

    private MotechDataRepository<T> repository;
    private HistoryService historyService;
    private TrashService trashService;
    private AllEntities allEntities;
    private SecurityMode securityMode;
    private Set<String> securityMembers;

    @PostConstruct
    public void initializeSecurityState() {
        Class clazz = repository.getClassType();
        String name = clazz.getName();
        Entity entity = allEntities.retrieveByClassName(name);

        securityMode = entity.getSecurityMode();
        securityMembers = entity.getSecurityMembers();
    }

    @Override
    @Transactional
    public T create(T object) {
        validateCredentials();
        setOwnerCreator(object);

        T created = repository.create(object);
        historyService.record(created);

        return created;
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
        return repository.retrieveAll(securityRestriction);
    }

    @Override
    @Transactional
    public List<T> retrieveAll(QueryParams queryParams) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.retrieveAll(queryParams, securityRestriction);
    }

    @Override
    @Transactional
    public T update(T object) {
        validateCredentials(object);

        setModificationFields(object, getUsername(), DateUtil.now());

        T updated = repository.update(object);
        historyService.record(updated);

        return updated;
    }

    @Override
    @Transactional
    public void delete(T object) {
        validateCredentials(object);

        boolean trashMode = trashService.isTrashMode();

        if (trashMode) {
            // move object to trash if trash mode is active
            trashService.moveToTrash(object);
        } else {
            // otherwise remove all historical data
            historyService.remove(object);
        }

        // independent of trash mode remove object. If trash mode is active then the same object
        // exists in the trash so this one is unnecessary.
        repository.delete(object);
    }

    @Override
    @Transactional
    public void delete(String primaryKeyName, Object value) {
        delete(retrieve(primaryKeyName, value));
    }

    @Override
    @Transactional
    public long count() {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.count(securityRestriction);
    }

    @Transactional
    protected List<T> retrieveAll(String[] parameters, Object[] values) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.retrieveAll(parameters, values, securityRestriction);
    }

    @Transactional
    protected List<T> retrieveAll(String[] parameters, Object[] values, QueryParams queryParams) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.retrieveAll(parameters, values, queryParams, securityRestriction);
    }

    @Transactional
    protected long count(String[] parameters, Object[] values) {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.count(parameters, values, securityRestriction);
    }

    protected InstanceSecurityRestriction validateCredentials() {
        return checkNonInstanceAccess();
    }

    protected InstanceSecurityRestriction validateCredentials(T instance) {
        InstanceSecurityRestriction restriction = checkNonInstanceAccess();
        if (!restriction.isEmpty()) {
            restriction = checkInstanceAccess(instance, restriction);
        }
        return restriction;
    }

    private InstanceSecurityRestriction checkNonInstanceAccess() {
        boolean authorized = false;
        String username = getUsername();

        if (securityMode == SecurityMode.EVERYONE) {
            authorized = true;
        } else if (securityMode == SecurityMode.USERS) {
            if (securityMembers.contains(username)) {
                authorized = true;
            }
        } else if (securityMode == SecurityMode.ROLES) {
            for (String role : getUserRoles()) {
                if (securityMembers.contains(role)) {
                    authorized = true;
                }
            }
        }

        if (!authorized && !securityMode.isIntanceRestriction()) {
            throw new SecurityException();
        }

        InstanceSecurityRestriction restriction = new InstanceSecurityRestriction();
        restriction.setByOwner(securityMode == SecurityMode.OWNER);
        restriction.setByCreator(securityMode == SecurityMode.CREATOR);

        return restriction;
    }

    private InstanceSecurityRestriction checkInstanceAccess(T instance, InstanceSecurityRestriction restriction) {
        T fromDb = repository.retrieve(getId(instance));

        String creator = (String) PropertyUtil.safeGetProperty(fromDb, "creator");
        String owner = (String) PropertyUtil.safeGetProperty(fromDb, "owner");

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

    protected String getUsername() {
        String username = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            username = authentication.getName();
        }

        return username;
    }

    protected List<String> getUserRoles() {
        List<String> roles = new ArrayList<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            roles.addAll(((MotechUserProfile) authentication.getDetails()).getRoles());
        }

        return roles;
    }

    protected void setOwnerCreator(T instance) {
        String username = getUsername();
        DateTime now = DateUtil.now();

        PropertyUtil.safeSetProperty(instance, Constants.Util.CREATOR_FIELD_NAME, username);
        PropertyUtil.safeSetProperty(instance, Constants.Util.OWNER_FIELD_NAME, username);
        PropertyUtil.safeSetProperty(instance, Constants.Util.CREATION_DATE_FIELD_NAME, now);

        setModificationFields(instance, username, now);
    }

    private void setModificationFields(T instance, String username, DateTime modificationTime) {
        PropertyUtil.safeSetProperty(instance, Constants.Util.MODIFIED_BY_FIELD_NAME, username);
        PropertyUtil.safeSetProperty(instance, Constants.Util.MODIFICATION_DATE_FIELD_NAME, modificationTime);
    }

    protected Object getId(T instance) {
        Field field = FieldUtils.getField(instance.getClass(), ID, true);
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            logger.error("Unable to retrieve object id", e);
            return null;
        }
    }

    @Autowired
    public void setRepository(MotechDataRepository<T> repository) {
        this.repository = repository;
    }

    @Autowired
    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }

    @Autowired
    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    @Autowired
    public void setTrashService(TrashService trashService) {
        this.trashService = trashService;
    }
}
