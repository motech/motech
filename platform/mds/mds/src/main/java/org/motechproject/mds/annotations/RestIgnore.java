package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>RestIgnore</code> annotation is used by developer to mark a field of entity
 * as not exposed over REST. By default all fields (including auto-generated ones) are
 * exposed. To ignore one of the auto-generated fields, it have to be declared in child
 * entity and marked with this annotation.
 * <p/>
 * This annotation is processed by {@link org.motechproject.mds.annotations.internal.RestIgnoreProcessor}
 *
 * @see org.motechproject.mds.annotations.internal.RestIgnoreProcessor
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestIgnore {

}
