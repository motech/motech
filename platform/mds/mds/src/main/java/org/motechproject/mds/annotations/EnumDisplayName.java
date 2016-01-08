package org.motechproject.mds.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>EnumDisplayName</code> annotation is used to point a field in enum variable that
 * contains value which should be displayed instead of its raw name. This annotation can be applied
 * on single enum values and enum sets.
 * For proper use of this annotation it should be applied to enums with provided constructor, field
 * containing display value and this fields correspondent 'getter' method.
 *
 * The discovery logic for this annotation is done in
 * {@link org.motechproject.mds.annotations.internal.FieldProcessor}.
 *
 * @see org.motechproject.mds.annotations.internal.FieldProcessor
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumDisplayName {

    /**
     * Points the field holding a value to be displayed.
     *
     * @return name of the field with a display value.
     */
    String enumField();
}

