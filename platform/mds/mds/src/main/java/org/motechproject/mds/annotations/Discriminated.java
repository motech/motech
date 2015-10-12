package org.motechproject.mds.annotations;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
public @interface Discriminated {

}
