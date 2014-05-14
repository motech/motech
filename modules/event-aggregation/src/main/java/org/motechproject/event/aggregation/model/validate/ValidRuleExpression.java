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
@Constraint(validatedBy = RuleExpressionValidator.class)
public @interface ValidRuleExpression {

    String message() default "not a valid expression";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
