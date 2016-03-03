package org.motechproject.tasks.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks classes destined to represent module task channels. Methods annotated with {@code @TaskAction} annotation
 * should be placed in class annotated with this annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TaskChannel {

    /**
     * Sets the name of the channel.
     *
     * @return  the name of the channel.
     */
    String channelName();

    /**
     * Sets the name of the channel source module.
     *
     * @return  the name of the module
     */
    String moduleName();

    /**
     * Sets the version of the module.
     *
     * @return  the version of the module
     */
    String moduleVersion();

}
