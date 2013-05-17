package org.motechproject.mrs.services;

import org.motechproject.mrs.domain.MRSConcept;

import java.util.List;

public interface MRSConceptAdapter {

    String resolveConceptUuidFromConceptName(String conceptName);

    /**
     * Saves a concept to the MRS system
     *
     * @param concept Object to be saved
     * @return Saved instance of the concept
     */
    MRSConcept saveConcept(MRSConcept concept);

    /**
     * Fetches a concept by the given concept id
     *
     * @param conceptId Value to be used to find a concept
     * @return Concept with the given concept id if exists
     */
    MRSConcept getConcept(String conceptId);

    /**
     * Searches for concepts in the MRS system by concept's name
     *
     * @param name     Name of the concept to be searched for
     * @return List of matched Concepts
     */
    List<? extends MRSConcept> search(String name);

    List<? extends MRSConcept> getAllConcepts();

    void deleteConcept(String conceptId);
    MRSConcept updateConcept(MRSConcept concept);

}