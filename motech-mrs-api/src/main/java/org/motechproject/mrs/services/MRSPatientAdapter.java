package org.motechproject.mrs.services;

import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.model.MRSPatient;

import java.util.Date;
import java.util.List;

/**
 * \defgroup mrs MRS
 */

/**
 * Interface for handling Patients
 */
public interface MRSPatientAdapter {
    /**
     * Saves a patient to the MRS system
     *
     * @param patient Object to be saved
     * @return Saved instance of the patient
     */
    MRSPatient savePatient(MRSPatient patient);

    /**
     * Finds a patient by Motech id and updates the patient's details in the MRS system
     *
     * @param patient Patient instance with updated values (MOTECH identifier cannot be changed)
     * @return The MOTECH identifier of the updated patient if successfully updated
     */
    MRSPatient updatePatient(MRSPatient patient);

    /**
     * Fetches a patient by the given patient id
     *
     * @param patientId Value to be used to find a patient
     * @return Patient with the given patient id if exists
     */
    MRSPatient getPatient(String patientId);

    /**
     * Fetches a patient by MOTECH id
     *
     * @param motechId Value to be used to find a patient
     * @return Patient with the given MOTECH id if exists
     */
    MRSPatient getPatientByMotechId(String motechId);

    /**
     * Searches for patients in the MRS system by patient's name and MOTECH id
     *
     * @param name     Name of the patient to be searched for
     * @param motechId Motech id of the patient to be searched for
     * @return List of matched Patients
     */
    List<MRSPatient> search(String name, String motechId);

    /**
     * Gets the age of a patient by MOTECH id
     * Deprecated: The caller should instead get Patient and get the age from that
     *
     * @param motechId Motech id of the patient
     * @return The age of the patient if found
     */
    @Deprecated
    Integer getAgeOfPatientByMotechId(String motechId);

    /**
     * Marks a patient as dead with the given date of death and comment
     *
     * @param motechId    Deceased patient's MOTECH id
     * @param conceptName Concept name for tracking deceased patients
     * @param dateOfDeath Patient's date of death
     * @param comment     Additional information for the cause of death
     * @throws PatientNotFoundException Exception when the expected Patient does not exist
     */
    void deceasePatient(String motechId, String conceptName, Date dateOfDeath, String comment) throws PatientNotFoundException;
}
