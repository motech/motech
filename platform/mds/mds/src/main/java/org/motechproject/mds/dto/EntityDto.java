package org.motechproject.mds.dto;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.SecurityMode;

import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.mds.util.SecurityUtil.getUserPermissions;
import static org.motechproject.mds.util.SecurityUtil.getUsername;

/**
 * The <code>EntityDto</code> class contains only basic information about an entity like id, name,
 * module and namespace.
 */
public class EntityDto {
    private Long id;
    private String className;
    private String name;
    private String module;
    private String bundleSymbolicName;
    private String namespace;
    private String tableName;
    private boolean recordHistory;
    private boolean readOnly;
    private boolean modified;
    private boolean outdated;
    private boolean nonEditable;
    private SecurityMode securityMode;
    private SecurityMode readOnlySecurityMode;
    private Set<String> securityMembers;
    private Set<String> readOnlySecurityMembers;
    private String superClass;
    private boolean abstractClass;
    private boolean securityOptionsModified;
    private Integer maxFetchDepth;
    private boolean readOnlyAccess;

    public EntityDto() {
        this(null, null, null, null, null, null, null, null);
    }

    public EntityDto(String className) {
        this(className, SecurityMode.EVERYONE, null);
    }

    public EntityDto(Long id, String className) {
        this(id, className, SecurityMode.EVERYONE, null);
    }

    public EntityDto(String className, SecurityMode securityMode, Set<String> securityMembers) {
        this(className, ClassName.getSimpleName(className), null, null, securityMode, securityMembers);
    }

    public EntityDto(Long id, String className, SecurityMode securityMode, Set<String> securityMembers) {
        this(id, className, ClassName.getSimpleName(className), null, null, securityMode, securityMembers);
    }

    public EntityDto(Long id, String className, String module, SecurityMode securityMode, Set<String> securityMembers) {
        this(id, className, ClassName.getSimpleName(className), module, null, securityMode, securityMembers);
    }

    public EntityDto(Long id, String className, String module, String namespace, SecurityMode securityMode, Set<String> securityMembers) {
        this(id, className, ClassName.getSimpleName(className), module, namespace, securityMode, securityMembers);
    }

    public EntityDto(String className, String name, String module, String namespace, SecurityMode securityMode, Set<String> securityMembers) {
        this(null, className, name, module, namespace, securityMode, securityMembers);
    }

    public EntityDto(Long id, String className, String name, String module, String namespace, SecurityMode securityMode, Set<String> securityMembers) {
        // to avoid PMD violation (ConstructorCallsOverridableMethod) we pass string representation of Object class name
        this(id, className, name, module, namespace, securityMode, securityMembers, "java.lang.Object");
    }

    public EntityDto(Long id, String className, String name, String module, String namespace, SecurityMode securityMode, Set<String> securityMembers, String superClass) {
        this(id, className, name, module, namespace, null, false, securityMode, securityMembers, null, null, superClass, false, false, null);
    }

    public EntityDto(Long id, String className, String name, String module, String namespace, String tableName, boolean recordHistory, SecurityMode securityMode, Set<String> securityMembers, SecurityMode readOnlySecurityMode, Set<String> readOnlySecurityMembers, String superClass, boolean abstractClass, boolean securityOptionsModified, String bundleSymbolicName) {
        this.id = id;
        this.className = className;
        this.name = name;
        this.module = module;
        this.namespace = namespace;
        this.tableName = tableName;
        this.recordHistory = recordHistory;
        this.securityMode = securityMode != null ? securityMode : SecurityMode.EVERYONE;
        this.securityMembers = securityMembers != null ? new HashSet<>(securityMembers) : new HashSet<String>();
        this.readOnlySecurityMode = readOnlySecurityMode;
        this.readOnlySecurityMembers = readOnlySecurityMembers;
        this.readOnly = isNotBlank(module) || isNotBlank(namespace);
        this.superClass = superClass;
        this.abstractClass = abstractClass;
        this.securityOptionsModified = securityOptionsModified;
        this.bundleSymbolicName = bundleSymbolicName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getBundleSymbolicName() {
        return bundleSymbolicName;
    }

    public void setBundleSymbolicName(String bundleSymbolicName) {
        this.bundleSymbolicName = bundleSymbolicName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isRecordHistory() {
        return recordHistory;
    }

    public void setRecordHistory(boolean recordHistory) {
        this.recordHistory = recordHistory;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public boolean isOutdated() {
        return outdated;
    }

    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }

    public SecurityMode getSecurityMode() {
        return securityMode;
    }

    public void setSecurityMode(SecurityMode securityMode) {
        this.securityMode = securityMode;
    }

    public Set<String> getSecurityMembers() {
        return securityMembers;
    }

    public void setSecurityMembers(Set<String> securityMembers) {
        this.securityMembers = securityMembers != null ? securityMembers : new HashSet<String>();
    }

    public SecurityMode getReadOnlySecurityMode() {
        return readOnlySecurityMode;
    }

    public void setReadOnlySecurityMode(SecurityMode readOnlySecurityMode) {
        this.readOnlySecurityMode = readOnlySecurityMode;
    }

    public Set<String> getReadOnlySecurityMembers() {
        return readOnlySecurityMembers;
    }

    public void setReadOnlySecurityMembers(Set<String> readOnlySecurityMembers) {
        this.readOnlySecurityMembers = readOnlySecurityMembers;
    }

    public String getSuperClass() {
        return defaultIfBlank(superClass, Object.class.getName());
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public boolean isAbstractClass() {
        return abstractClass;
    }

    public void setAbstractClass(boolean abstractClass) {
        this.abstractClass = abstractClass;
    }

    public boolean isSecurityOptionsModified() {
        return securityOptionsModified;
    }

    public void setSecurityOptionsModified(boolean securityOptionsModified) {
        this.securityOptionsModified = securityOptionsModified;
    }

    public Integer getMaxFetchDepth() {
        return maxFetchDepth;
    }

    public void setMaxFetchDepth(Integer maxFetchDepth) {
        this.maxFetchDepth = maxFetchDepth;
    }

    public boolean isNonEditable() {
        return nonEditable;
    }

    public void setNonEditable(boolean nonEditable) {
        this.nonEditable = nonEditable;
    }

    public boolean isReadOnlyAccess() {
        return readOnlyAccess;
    }

    public void setReadOnlyAccess(boolean readOnlyAccess) {
        this.readOnlyAccess = readOnlyAccess;
    }

    @JsonIgnore
    public boolean isDDE() {
        return StringUtils.isNotBlank(module);
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

    public boolean checkIfUserHasOnlyReadAccessAuthorization() {
        return (!hasAccessToEntityFromSecurityMode(securityMode, securityMembers) && hasAccessToEntityFromSecurityMode(readOnlySecurityMode, readOnlySecurityMembers));
    }

    public boolean hasAccessToEntityFromSecurityMode(SecurityMode mode, Set<String> members) {
        boolean authorized = false;
        String username = getUsername();
        if(mode == null) {
            authorized = false;
        } else if (mode == SecurityMode.EVERYONE) {
            authorized = true;
        } else if (mode == SecurityMode.USERS) {
            if (members.contains(username)) {
                authorized = true;
            }
        } else if (mode == SecurityMode.PERMISSIONS) {
            for (String permission : getUserPermissions()) {
                if (members.contains(permission)) {
                    authorized = true;
                }
            }
        } else if (mode == SecurityMode.NO_ACCESS) {
            authorized = false;
        } else if (mode.isInstanceRestriction()) {
            authorized = true;
        }
        return authorized;
    }
}
