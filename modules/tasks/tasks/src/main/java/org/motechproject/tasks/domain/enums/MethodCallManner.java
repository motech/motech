package org.motechproject.tasks.domain.enums;

import org.motechproject.tasks.domain.mds.channel.ActionEvent;

/**
 * The <code>MethodCallManner</code> enumerates possible call manners of an <code>ActionEvent</code> service method.
 * It also implies expected signature of this method.
 *
 * @see ActionEvent
 */
public enum MethodCallManner {
    /**
     * When using this call manner, the parameters are evaluated, casted to appropriate types and then passed to the
     * service method in specified order as a regular java method parameters.
     */
    NAMED_PARAMETERS,

    /**
     * When using this call manner, the parameters are evaluated, casted to appropriate types and then wrapped with
     * a Map in which keys corresponds to parameters names and values corresponds to parameters values. Map in this
     * form gets passed to the service method.
     */
    MAP
}
