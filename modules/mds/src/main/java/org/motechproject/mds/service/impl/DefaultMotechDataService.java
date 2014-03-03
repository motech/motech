package org.motechproject.mds.service.impl;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.ex.SecurityException;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.InstanceSecurityRestriction;
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
    private Class<? extends T> historyClass;
    private HistoryService historyService;
    private AllEntities allEntities;

    protected DefaultMotechDataService() {
        this(null);
    }

    protected DefaultMotechDataService(Class<? extends T> historyClass) {
        this.historyClass = historyClass;
    }

    @Override
    @Transactional
    public T create(T object) {
        validateCredentials();
        setOwnerCreator(object);
        T created = repository.create(object);
        historyService.record(historyClass, created);

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
        T updated = repository.update(object);
        historyService.record(historyClass, updated);

        return updated;
    }

    @Override
    @Transactional
    public void delete(T object) {
        validateCredentials(object);
        repository.delete(object);
    }

    @Override
    @Transactional
    public void delete(String primaryKeyName, Object value) {
        T instance = retrieve(primaryKeyName, value);
        repository.delete(instance);
    }

    @Override
    @Transactional
    public long count() {
        InstanceSecurityRestriction securityRestriction = validateCredentials();
        return repository.count(securityRestriction);
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
        Class clazz = repository.getClassType();
        String name = ClassName.getClassName(clazz.getName());
        Entity entity = allEntities.retrieveByClassName(name);
        SecurityMode mode = entity.getSecurityMode();

        boolean authorized = false;

        String username = getUsername();

        if (mode.equals(SecurityMode.EVERYONE)) {
            authorized = true;
        } else if (mode.equals(SecurityMode.USERS)) {
            Set<String> users = entity.getSecurityMembers();
            if (users.contains(username)) {
                authorized = true;
            }
        } else if (mode.equals(SecurityMode.ROLES)) {
            Set<String> roles = entity.getSecurityMembers();
            for (String role : getUserRoles()) {
                if (roles.contains(role)) {
                    authorized = true;
                }
            }
        }

        if (!authorized && !mode.isIntanceRestriction()) {
            throw new SecurityException();
        }

        InstanceSecurityRestriction restriction = new InstanceSecurityRestriction();
        restriction.setByOwner(mode == SecurityMode.OWNER);
        restriction.setByOwner(mode == SecurityMode.CREATOR);

        return restriction;
    }


    private InstanceSecurityRestriction checkInstanceAccess(T instance, InstanceSecurityRestriction restriction) {
        String creator = null;
        String owner = null;

        T fromDb = repository.retrieve(getId(instance));

        try {
            creator = (String) PropertyUtils.getProperty(fromDb, "creator");
            owner = (String) PropertyUtils.getProperty(fromDb, "owner");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Failed to resolve object creator or owner. Instance lacks necessary fields.", e);
        }

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
        try {
            String username = getUsername();
            PropertyUtils.setProperty(instance, "creator", username);
            PropertyUtils.setProperty(instance, "owner", username);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error("Unable to set objects creator", e);
        }
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
}
