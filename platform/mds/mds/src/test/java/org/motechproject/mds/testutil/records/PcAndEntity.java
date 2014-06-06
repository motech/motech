package org.motechproject.mds.testutil.records;

import org.motechproject.mds.annotations.Entity;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

/**
 * Has both {@link javax.jdo.annotations.PersistenceCapable} and {@link org.motechproject.mds.annotations.Entity}
 * annotations. Annotation values should be taken from the {@link javax.jdo.annotations.PersistenceCapable} annotation
 * if it is defined.
 */
@Entity
@PersistenceCapable(detachable = "false", identityType = IdentityType.APPLICATION, catalog = "testCatalog")
public class PcAndEntity {
}
