package org.motechproject.mds.annotations;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>@Entity</code> annotation is used by developers to point classes, that should be
 * mapped as Motech Dataservices Entities. The discovery logic for this annotation is done in
 * <code>SeussAnnotationProcessor</code>
 *
 * @see org.motechproject.mds.annotations.internal.SeussAnnotationProcessor
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
public @interface Entity {

    String name() default "";

    String module() default "";

    String namespace() default "";

}
