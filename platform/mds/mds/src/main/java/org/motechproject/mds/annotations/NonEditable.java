package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>NonEditable</code> annotation is used to mark a field non-editable via UI. The discovery logic for
 * this annotation is done in  {@link org.motechproject.mds.annotations.internal.NonEditableProcessor}.
 *
 * Only fields, 'getter' or 'setter' methods can have this annotation for other methods this
 * annotation is omitted.
 *
 * @see org.motechproject.mds.annotations.internal.NonEditableProcessor
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NonEditable {

    boolean display() default true;
}
