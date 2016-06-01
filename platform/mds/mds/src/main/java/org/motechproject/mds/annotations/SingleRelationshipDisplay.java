package org.motechproject.mds.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * The <code>SingleRelationshipDisplay</code> is used to provide details about how
 * a related field should be displayed in the UI. These details are saved as field settings.
 * These settings can also be editable through UI.
 * <p/>
 * It should be used on 1:1 or M:1 relationship fields.
 */

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SingleRelationshipDisplay {

    /**
     * Sets whether the related field grid should be expandable by default.
     *
     */
    boolean expandByDefault() default true;

    /**
     * Sets whether creating a new instance of the related Entity should be allowed.
     * */
    boolean allowAddingNew() default true;

    /**
     * Sets whether selecting an existing instance of the related Entity should be allowed.
     * */
    boolean allowAddingExisting() default true;
}
