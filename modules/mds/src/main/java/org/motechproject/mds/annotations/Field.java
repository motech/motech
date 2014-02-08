package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.apache.commons.lang.StringUtils.EMPTY;

/**
 * The <code>Field</code> annotation is used by developers to point fields, that should be mapped
 * as entity fields. The discovery logic for this annotation is done in
 * {@link org.motechproject.mds.annotations.internal.FieldProcessor}.
 * <p/>
 * Only fields, 'getter' or 'setter' methods can have this annotation for other methods this
 * annotation is omitted.
 *
 * @see org.motechproject.mds.annotations.internal.FieldProcessor
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Field {

    /**
     * Sets the display name of the field. If this property is blank, the display name will be
     * equal to the name of the field.
     *
     * @return the display name of the field.
     */
    String displayName() default EMPTY;

    /**
     * Sets the name of the field. If this property is blank, the name will be equal to the name
     * of the field.
     *
     * @return the name of the field.
     */
    String name() default EMPTY;

    /**
     * Sets if the field is required and it always should have some value different from
     * {@code null}.
     *
     * @return true if the field should have some value; otherwise false.
     */
    boolean required() default false;

    /**
     * Sets the default value which should be placed into the field if there is no any other value.
     *
     * @return the default value for the field.
     */
    String defaultValue() default EMPTY;

    /**
     * Sets the tooltip that will help to understand the purpose of the field.
     *
     * @return the field tooltip.
     */
    String tooltip() default EMPTY;

}
