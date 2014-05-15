package org.motechproject.event.aggregation.model.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PeriodValidator.class)
public @interface ValidPeriod {

    String message() default "Not a valid period value";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
