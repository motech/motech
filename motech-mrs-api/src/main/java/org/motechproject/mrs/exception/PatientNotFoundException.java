package org.motechproject.mrs.exception;

/**
 * Thrown when a Patient is null while performing operations on it.
 * <ul>
 *     <li>For e.g. in setting the patient as deceased, when Patient is not found this exception is thrown</li>
 * </ul>
 */
public class PatientNotFoundException extends Exception {
    public PatientNotFoundException(String message) {
        super(message);
    }
}
