package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>UIFilterable</code> annotation is used by developers to mark a field that allow users
 * to filter a list of objects by the values in the field. The discovery logic for this annotation
 * is done in {@link org.motechproject.mds.annotations.internal.UIFilterableProcessor}.
 * <p/>
 * Only fields, 'getter' or 'setter' methods can have this annotation for other methods this
 * annotation is omitted. Also this annotation is permitted on fields of type Date, Boolean or List.
 *
 * @see org.motechproject.mds.annotations.internal.UIFilterableProcessor
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UIFilterable {

}
