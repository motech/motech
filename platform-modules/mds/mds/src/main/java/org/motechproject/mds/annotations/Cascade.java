package org.motechproject.mds.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>Cascade</code> annotation is used to set correct cascade properties for
 * the given field that is a relationship.
 *
 * @see org.motechproject.mds.annotations.internal.FieldProcessor
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cascade {

    /**
     * Sets whether the related persistable object should also be persisted when this object is
     * persisted. By default it is set to {@code true}.
     * <p/>
     * <pre>
     * {@code
     * Car car = new Car("Shelby Mustang GT500");
     * DrivingLicense license = new DrivingLicense("011234BX4J");
     *
     * Owner bob = new Owner("Bob Smith");
     * bob.setLicense(license);
     * bob.setCars(Arrays.asList(car));
     *
     * makePersistent(bob); // Should "bob" know about "license" and "cars"?
     * }
     * </pre>
     *
     * @return {@code true} if a related persistable object should also be persisted; otherwise
     * {@code false}
     */
    boolean persist() default true;

    /**
     * Sets whether the related persistable object should also be updated when this object is
     * updated. By default it is set to {@code true}.
     * <p/>
     * <pre>
     * {@code
     * DrivingLicense license2 = new DrivingLicense("233424BX4J");
     *
     * Owner bob = (Owner) getObjectById(id);
     * bob.setLicense(license2); // Should "bob" know about new "license2"?
     * }
     * </pre>
     *
     * @return {@code true} if a related persistable object should also be persisted; otherwise
     * {@code false}
     */
    boolean update() default true;

    /**
     * Sets whether the related persistable object should also be deleted when this object is
     * deleted. By default it is set to {@code false}.
     * <p/>
     * <pre>
     * {@code
     * // suppose that the "bob" has a "licence" and a list of "cars"
     * Owner bob = (Owner) getObjectById(id);
     *
     * deletePersistent(bob); // Should "license" and "cars" also be removed?
     * }
     * </pre>
     *
     * @return {@code true} if a related persistable object should also be removed; otherwise
     * {@code false}
     */
    boolean delete() default false;

}
