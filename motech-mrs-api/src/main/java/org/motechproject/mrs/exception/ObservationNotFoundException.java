package org.motechproject.mrs.exception;

/**
 * Thrown when a MRSObservation is null while performing operations on it.
 * <ul>
 *     <li>For e.g. in voiding an MRSObservation, if observation is not found this exception is thrown</li>
 * </ul>
 */
public class ObservationNotFoundException extends Exception {
    public ObservationNotFoundException(String message) {
        super(message);
    }
}
