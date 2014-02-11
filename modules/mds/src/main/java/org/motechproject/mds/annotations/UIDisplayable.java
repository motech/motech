package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>UIDisplayable</code> annotation is used by developers to mark a field as being in the
 * default display for a listing of objects. The discovery logic for this annotation is done in
 * {@link org.motechproject.mds.annotations.internal.UIDisplayableProcessor}.
 * <p/>
 * Only fields, 'getter' or 'setter' methods can have this annotation for other methods this
 * annotation is omitted.
 *
 * @see org.motechproject.mds.annotations.internal.UIDisplayableProcessor
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UIDisplayable {

    /**
     * Sets the position on which the given field should be visible in the default display for a
     * listing of objects. In default the field is added in the end of the field list.
     *
     * @return the field position in the default display for a listing of objects.
     */
    long position() default -1L;
}
