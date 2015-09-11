package org.motechproject.mds.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.NonEditable;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import static org.motechproject.mds.util.Constants.Util.DATANUCLEUS;
import static org.motechproject.mds.util.Constants.Util.FALSE;
import static org.motechproject.mds.util.Constants.Util.TRUE;

@Entity
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "instanceVersion",
        extensions={@Extension(vendorName = DATANUCLEUS, key="field-name", value="instanceVersion")})
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = TRUE)
public abstract class MdsVersionedEntity extends MdsEntity {

    @Field(displayName = "Instance Version")
    @NonEditable
    @Persistent(defaultFetchGroup = TRUE, cacheable = FALSE)
    private Long instanceVersion;

    public Long getInstanceVersion() {
        return instanceVersion;
    }

    public void setInstanceVersion(Long instanceVersion) {
        this.instanceVersion = instanceVersion;
    }

    @NotPersistent
    public void someNewValue(Long instanceVersion) {
        this.instanceVersion = instanceVersion;
    }

}
