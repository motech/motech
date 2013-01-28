package org.motechproject.mrs.services;

import java.util.List;
import org.joda.time.DateTime;
import org.motechproject.mrs.domain.Attribute;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.exception.MRSException;

/**
 * Interface to save, update, remove and retrieve person entities
 */
public interface PersonAdapter {

    /**
     * Persists a person object
     * @param person The person to be saved 
     * @throws MRSException Thrown if the person object violated constraints in the implementing module
     */
    void addPerson(Person person) throws MRSException;

    /**
     * Creates and persists a person object from field values
     * @param personId The id of the person
     * @param firstName The person's first name
     * @param lastName The person's last name
     * @param dateOfBirth The person's date of birth
     * @param gender The person's gender
     * @param address The address of the person
     * @param attributes A list of attributes for the person
     * @throws MRSException Thrown if the person object violated constraints in the implementing module
     */
    void addPerson(String personId, String firstName, String lastName, DateTime dateOfBirth, String gender,
            String address, List<Attribute> attributes) throws MRSException;

    /**
     * Updates a person object
     * @param person The person to update
     */
    void updatePerson(Person person);

    /**
     * Removes a person from storage
     * @param person The person to remove
     */
    void removePerson(Person person);

    /**
     * Retrieves a list of all person objects
     * @return A list of all persons, of a type from the implementing module
     */
    List<? extends Person> findAllPersons();

    /**
     * Finds a list of persons by a particular id
     * @param personId The id of the person to search for
     * @return A list of all persons of given id, of a type from the implementing module
     */
    List<? extends Person> findByPersonId(String personId);

    /**
     * Removes all persons from storage
     */
    void removeAll();

}
