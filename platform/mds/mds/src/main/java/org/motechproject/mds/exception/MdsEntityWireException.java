package org.motechproject.mds.exception;

/**
 * Exception, that informs about a problem when an entity is outside OSGi exported package.
 * It contains message with problem description and possible solutions.
 */
public class MdsEntityWireException extends MdsException {

    private static final long serialVersionUID = 1097291331451418690L;

    public static final String SOLUTION_MESSAGE = "Failed to resolve entities in the generated MDS Entities Bundle. " +
            "This may be caused by incorrect (or lacking) OSGi package export declared within the bundle manifest ," +
            "(the pom if you are using felix-bundle-plugin) or improper entity placement. Every package you want to wire " +
            "should be explicitly exported in OSGi or MDS will not be able to access the class of the Entity.";

    public MdsEntityWireException(Throwable cause) {
        super(SOLUTION_MESSAGE, cause);
    }
}
