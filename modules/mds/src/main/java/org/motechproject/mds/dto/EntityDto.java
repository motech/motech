package org.motechproject.mds.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.motechproject.mds.util.ClassName;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * The <code>EntityDto</code> class contains only basic information about an entity like id, name,
 * module and namespace.
 */
public class EntityDto {
    private Long id;
    private String className;
    private String name;
    private String module;
    private String namespace;
    private boolean readOnly;
    private boolean modified;

    public EntityDto() {
        this(null, null, null, null, null);
    }

    public EntityDto(String className) {
        this(className, ClassName.getSimpleName(className), null, null);
    }

    public EntityDto(Long id, String className) {
        this(id, className, ClassName.getSimpleName(className), null, null);
    }

    public EntityDto(Long id, String className, String module) {
        this(id, className, ClassName.getSimpleName(className), module, null);
    }

    public EntityDto(Long id, String className, String module, String namespace) {
        this(id, className, ClassName.getSimpleName(className), module, namespace);
    }

    public EntityDto(String className, String name, String module, String namespace) {
        this(null, className, name, module, namespace);
    }

    public EntityDto(Long id, String className, String name, String module, String namespace) {
        this.id = id;
        this.className = className;
        this.name = name;
        this.module = module;
        this.namespace = namespace;
        this.readOnly = isNotBlank(module) || isNotBlank(namespace);
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

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
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
