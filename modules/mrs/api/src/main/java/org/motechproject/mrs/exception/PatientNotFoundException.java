package org.motechproject.mrs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * Thrown when a Patient is null while performing operations on it.
 * <ul>
 *     <li>For e.g. in setting the patient as deceased, when Patient is not found this exception is thrown</li>
 * </ul>
 */

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PatientNotFoundException extends Exception {
    public PatientNotFoundException(String message) {
        super(message);
    }
    public PatientNotFoundException(Exception e) {
        super(e.getMessage());
    }

}
