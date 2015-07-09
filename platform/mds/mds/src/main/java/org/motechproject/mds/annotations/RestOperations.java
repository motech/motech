package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>RestOperations</code> annotation is used to specify which
 * CRUD operations should be enabled for entity. RestOperations value is an array of
 * one or more values specified in {@link RestOperation}
 * enum, that is: CREATE, READ, UPDATE, DELETE. There is also one special value - ALL.
 * When provided, all CRUD operations are enabled for entity, regardless of presence
 * of other values.
 * <p/>
 * This annotation is processed by
 * {@link org.motechproject.mds.annotations.internal.RestOperationsProcessor}
 * and can be applied only to class which is also annotated with
 * {@link org.motechproject.mds.annotations.Entity}. It has no effect otherwise.
 *
 * @see RestOperation
 * @see org.motechproject.mds.annotations.internal.RestOperationsProcessor
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestOperations {
    RestOperation[] value();
}
