package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Thanks to this annotation, developers can point public fields and getter/setter method of a private field
 * that should not be added to the schema definition of an entity.
 * By default, all public fields of an object are added.
 * The discovery logic for this annotation is done in the FieldProcessor
 *
 * @see org.motechproject.mds.annotations.internal.FieldProcessor
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ignore {

}
