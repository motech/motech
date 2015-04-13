package org.motechproject.mds.annotations;

import org.motechproject.mds.util.SecurityMode;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>Access</code> annotation is used to specify security options of an entity.
 * The discovery logic for this annotation is done in
 * {@link org.motechproject.mds.annotations.internal.EntityProcessor}
 *
 * @see org.motechproject.mds.annotations.internal.EntityProcessor
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Access {

    /**
     * Sets the security mode for the entity.
     *
     * @return the access security mode.
     */
    SecurityMode value();

    /**
     * Sets the security members. This attribute can be only used with USERS or ROLES security mode.
     *
     * @return the security members.
     */
    String[] members() default {};

}
