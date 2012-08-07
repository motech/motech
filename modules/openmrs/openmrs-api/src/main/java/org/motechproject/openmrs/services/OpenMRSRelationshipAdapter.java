package org.motechproject.openmrs.services;

import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adapter to manage OpenMRS Relationships
 */
@Component
public class OpenMRSRelationshipAdapter {

    @Autowired
    private PersonService personService;
    static final String PARENT_CHILD_RELATIONSHIP = "Parent/Child";
    static final String REASON = "Removed in web or mobile form";

    /**
     * Creates a Mother-Child relationship between two Patients (Mother & Child)
     *
     * @param motherId Mother's MOTECH Id
     * @param childId  Child's MOTECH Id
     * @return the Relationship object from OpenMRS
     */
    public Relationship createMotherChildRelationship(String motherId, String childId) {
        RelationshipType parentChildRelationshipType = getRelationshipTypeByName(PARENT_CHILD_RELATIONSHIP);
        Relationship relationship = new Relationship(personService.getPerson(Integer.valueOf(motherId)),
                personService.getPerson(Integer.valueOf(childId)), parentChildRelationshipType);
        return personService.saveRelationship(relationship);
    }

    /**
     * Voids the Mother-Child relationship
     *
     * @param childId Child's MOTECH Id
     * @return the voided Relationship of OpenMRS
     */
    public Relationship voidRelationship(String childId) {
        Relationship relationship = getMotherRelationship(childId);
        relationship = personService.voidRelationship(relationship, REASON);
        return personService.saveRelationship(relationship);
    }

    /**
     * Updates the Mother's MOTECH Id for the Child in the Relationship
     *
     * @param motherId updated MOTECH Id of the Mother
     * @param childId Child's MOTECH Id
     * @return updated OpenMRS Relationship
     */
    public Relationship updateMotherRelationship(String motherId, String childId) {
        Relationship motherRelationship = getMotherRelationship(childId);
        Person updatedMother = personService.getPerson(Integer.valueOf(motherId));
        motherRelationship.setPersonA(updatedMother);
        return personService.saveRelationship(motherRelationship);
    }

    /**
     * Gets the Relationship Object, given child's MOTECH Id
     * @param childId Child's MOTECH Id
     * @return OpenMRS Relationship Object if found, else null
     */
    public Relationship getMotherRelationship(String childId) {
        RelationshipType parentChildType = getRelationshipTypeByName(PARENT_CHILD_RELATIONSHIP);
        List<Relationship> parentRelations
                = personService.getRelationships(null, personService.getPerson(Integer.valueOf(childId)), parentChildType);
        return (!parentRelations.isEmpty()) ? parentRelations.get(0) : null;
    }

    private RelationshipType getRelationshipTypeByName(String relationshipType) {
        return personService.getRelationshipTypeByName(relationshipType);
    }
}
