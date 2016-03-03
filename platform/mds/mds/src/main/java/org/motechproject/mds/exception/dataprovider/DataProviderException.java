package org.motechproject.mds.exception.dataprovider;

import org.motechproject.mds.exception.MdsException;

/**
 * The <code>DataProviderException</code> exception signals a situation in which there were some
 * problems with executing lookup in <code>MDSDataProvider</code>.
 */
public class DataProviderException extends MdsException {

    private static final long serialVersionUID = 4979147819786302836L;

    public DataProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
