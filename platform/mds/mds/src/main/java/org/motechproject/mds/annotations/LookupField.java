package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>LookupField</code> annotation allows developers to point fields in Lookup method, that
 * should be mapped as Lookup fields for Developer Defined Lookup. The discovery logic for this
 * annotation is done in {@link org.motechproject.mds.annotations.internal.LookupProcessor}
 *
 * @see org.motechproject.mds.annotations.internal.LookupProcessor
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LookupField {

    /**
     * Sets the name of the lookup field. If this value is blank, the lookup field name is equal to
     * the name of the related parameter in the method with the
     * {@link org.motechproject.mds.annotations.Lookup} annotation.
     *
     * @return the lookup field name.
     */
    String name() default "";

    /**
     * Allows specifying a custom operator for the given lookup. This operator will be used in the JDO
     * query constructed for this lookup. Specifying custom operators allows building more elaborate lookups then
     * those that use only equals.
     *
     *  @return the custom operator to be used in the JDO query.
     */
    String customOperator() default "";
}
