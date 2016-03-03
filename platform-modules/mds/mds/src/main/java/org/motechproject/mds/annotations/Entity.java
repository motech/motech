package org.motechproject.mds.annotations;

import org.motechproject.mds.util.Constants;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>Entity</code> annotation is used to point classes, that should be
 * mapped as Motech Dataservices Entities. The discovery logic for this annotation is done in
 * {@link org.motechproject.mds.annotations.internal.EntityProcessor}
 *
 * @see org.motechproject.mds.annotations.internal.EntityProcessor
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
public @interface Entity {

    /**
     * Sets the name of the entity. By default the name is equal to simple name of the class.
     *
     * @return the name of the entity.
     */
    String name() default "";

    /**
     * Sets the name of the module in which the entity is defined. By default the module name
     * will be equal to bundle name. If this value is unavailable, the module name will be equal
     * to bundle symbolic name. If both values are unavailable, the module name will be equal to
     * {@code null} and entity will be visible as a MDS entity.
     *
     * @return the name of module.
     */
    String module() default "";

    /**
     * Sets the name of the namespace in which the entity is defined. There is no mechanism to
     * add default value for namespace.
     *
     * @return the name of namespace.
     */
    String namespace() default "";

    /**
     * Sets the name of the table which is used for entity.
     *
     * @return the name of table.
     */
    String tableName() default "";

    /**
     * Sets whether instances should be editable for this entity.
     *
     * @return true if instances should be nonEditable otherwise false.
     */
    boolean nonEditable() default false;

    /**
     * Sets whether instance history should be recorded for this entity
     * add default value for history.
     *
     * @return true if history should be recorded otherwise false.
     */
    boolean recordHistory() default false;

    /**
     * Sets the max fetch depth that will be used when fetching instances of this entity.
     * For example, setting a fetch depth value of 3 will mean that MDS will go 3 levels deep max
     * when fetching fields of instances - meaning that relationships further down the ladder won't be fetched.
     * The default value will leave this as the global MDS default.
     *
     * @return the maximum fetch depth that will be used for the given entity
     */
    int maxFetchDepth() default Constants.FetchDepth.MDS_DEFAULT;
}
