package org.motechproject.tasks.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks methods that should be treated as task actions. Methods marked with this annotation must be placed in a class
 * annotated with {@code @TaskChannel} annotation.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TaskAction {

    /**
     * Sets the display name of the action.
     *
     * @return  the display name of the action
     */
    String displayName();

}
