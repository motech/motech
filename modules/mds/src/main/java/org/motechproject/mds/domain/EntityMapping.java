package org.motechproject.mds.domain;

import org.motechproject.mds.dto.EntityDto;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * The <code>EntityMapping</code> class contains basic information about an entity. This class is
 * related with table in database with the same name.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
public class EntityMapping {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent
    private String className;

    @Persistent
    private String module;

    @Persistent
    private String namespace;

    public EntityMapping() {
        this(null);
    }

    public EntityMapping(String className) {
        this(className, null, null);
    }

    public EntityMapping(String className, String module, String namespace) {
        this.className = className;
        this.module = module;
        this.namespace = namespace;
    }

    public EntityDto toDto() {
        String simpleName = className.substring(className.lastIndexOf('.') + 1);

        return new EntityDto(id, simpleName, module, namespace);
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

    @NotPersistent
    public boolean isReadOnly() {
        return isNotBlank(module) || isNotBlank(namespace);
    }
}
