/**
 * \defgroup openMRS Open MRS
 */
/**
 * \ingroup openMRS
 * Services offered by Open MRS
 */
package org.motechproject.openmrs.services;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Adapter class to get Concepts in OpenMRS
 */
public class OpenMRSConceptAdapter {

    @Autowired
    private ConceptService conceptService;

    /**
     * Fetches the Concept object of openMRS
     * @param conceptName Name of the concept
     * @return OpenMRS concept
     */
    public Concept getConceptByName(String conceptName) {
        return conceptService.getConcept(conceptName);
    }
}
