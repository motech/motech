package org.motechproject.mds.domain;

import org.motechproject.mds.dto.AvailableTypeDto;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import static org.motechproject.mds.util.Constants.Util.TRUE;

/**
 * The <code>AvailableType</code> class contains a default name for a field with the given type. It
 * should be used only on MDS UI.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = TRUE)
public class AvailableType {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent
    private String defaultName;

    @Column(name = "TYPE_ID")
    private Type type;

    public AvailableType() {
        this(null, null, null);
    }

    public AvailableType(Long id, String defaultName, Type type) {
        this.id = id;
        this.defaultName = defaultName;
        this.type = type;
    }

    public AvailableTypeDto toDto() {
        return new AvailableTypeDto(id, defaultName, type.toDto());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
