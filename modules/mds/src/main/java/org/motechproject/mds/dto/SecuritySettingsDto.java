package org.motechproject.mds.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>SecuritySettingsDto</code> contains information about security settings of an entity.
 */
public class SecuritySettingsDto {
    private Long id;
    private Long entityId;
    private AccessOptions access;
    private List<String> users;
    private List<String> roles;

    public SecuritySettingsDto() {
        this(null, null, null, null, null);
    }

    public SecuritySettingsDto(Long id, Long entityId, AccessOptions access, List<String> users, List<String> roles) {
        this.id = id;
        this.entityId = entityId;
        this.access = access;
        this.users = null == users ? new ArrayList<String>() : users;
        this.roles = null == roles ? new ArrayList<String>() : roles;
    }

    public void addUser(String user) {
        if (!users.contains(user)) {
            users.add(user);
        }
    }

    public void removeUser(String user) {
        users.remove(user);
    }

    public void removeAllUsers() {
        users.clear();
    }

    public void addRole(String role) {
        if (!roles.contains(role)) {
            roles.add(role);
        }
    }

    public void removeRole(String role) {
        roles.remove(role);
    }

    public void removeAllRoles() {
        roles.clear();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public AccessOptions getAccess() {
        return access;
    }

    public void setAccess(AccessOptions access) {
        this.access = access;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = null == users ? new ArrayList<String>() : users;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = null == roles ? new ArrayList<String>() : roles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
