package org.motechproject.mds.testutil.records;

import javax.jdo.annotations.PersistenceCapable;

/**
 * This class has only the {@link javax.jdo.annotations.PersistenceCapable} annotation.
 * Annotation readers should recognize it correctly nonetheless.
 */
@PersistenceCapable(detachable = "true", cacheable = "false")
public class JustPc {
}
