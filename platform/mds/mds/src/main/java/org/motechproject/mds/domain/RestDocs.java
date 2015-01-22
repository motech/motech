package org.motechproject.mds.domain;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.sql.Clob;

/**
 * Domain class for persisting the generated Rest documentation.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class RestDocs {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent
    private Clob documentation;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Clob getDocumentation() {
        return documentation;
    }

    public void setDocumentation(Clob documentation) {
        this.documentation = documentation;
    }
}
