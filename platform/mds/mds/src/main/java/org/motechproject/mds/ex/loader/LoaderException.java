package org.motechproject.mds.ex.loader;

import org.motechproject.mds.ex.MdsException;

/**
 * The <code>LoaderException</code> exception signals situations in which there were problems with
 * correct loading the given class or its dependencies.
 */
public class LoaderException extends MdsException {

    public LoaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
