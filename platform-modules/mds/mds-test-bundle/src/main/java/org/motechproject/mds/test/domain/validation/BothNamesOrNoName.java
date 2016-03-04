package org.motechproject.mds.test.domain.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { ValidationSampleValidator.class })
@Documented
public @interface BothNamesOrNoName {

    String message() default "Name and last name must be both set or both null";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
