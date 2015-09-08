package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>IndexedManyToMany</code> annotation is used to point ManyToMany indexed relation fields
 * and to avoid using of @Persistent(mappedBy = "relatedField").
 * Only fields can have this annotation.
 *
 * @see org.motechproject.mds.annotations.internal.FieldProcessor
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IndexedManyToMany {

    /**
     * Sets the related field name. This field is required.
     *
     * @return the related field name.
     */
    String relatedField();
}
