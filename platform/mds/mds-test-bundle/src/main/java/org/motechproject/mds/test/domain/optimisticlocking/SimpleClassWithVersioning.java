package org.motechproject.mds.test.domain.optimisticlocking;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.NonEditable;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import static org.motechproject.mds.util.Constants.Util.DATANUCLEUS;

@Entity
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version",
        extensions={@Extension(vendorName = DATANUCLEUS, key="field-name", value="version")})
public class SimpleClassWithVersioning {

    public SimpleClassWithVersioning(String stringField) {
        this.stringField = stringField;
    }

    @Field
    private String stringField;

    @Field
    @NonEditable
    private Long version;

    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
