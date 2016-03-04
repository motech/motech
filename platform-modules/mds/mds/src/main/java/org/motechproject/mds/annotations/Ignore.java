package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Thanks to this annotation, developers can point class fields that should not be included in
 * entity schema definition. To work properly, it is required that either a field or a getter is
 * marked with this annotation.
 * By default, all public fields of an object are included.
 * The discovery logic for this annotation is done in the FieldProcessor and partially in
 * MdsIgnoreAnnotationHandler.
 *
 * @see org.motechproject.mds.annotations.internal.FieldProcessor
 * @see org.motechproject.mds.jdo.MdsIgnoreAnnotationHandler
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ignore {

}
