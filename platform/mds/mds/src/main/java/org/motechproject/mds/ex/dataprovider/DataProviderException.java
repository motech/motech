package org.motechproject.mds.ex.dataprovider;

/**
 * The <code>DataProviderException</code> exception signals a situation in which there were some
 * problems with executing lookup in <code>MDSDataProvider</code>.
 *
 * @see org.motechproject.mds.MDSDataProvider
 */
public class DataProviderException extends RuntimeException {

    private static final long serialVersionUID = 4979147819786302836L;

    public DataProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
