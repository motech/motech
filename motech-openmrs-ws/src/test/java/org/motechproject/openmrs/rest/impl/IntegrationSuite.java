package org.motechproject.openmrs.rest.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Integration Tests should be run against a clean OpenMRS database If there is existing data in the database, it could
 * cause test failures
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ MRSEncounterAdapterImplIT.class, MRSFacilityAdapterImplIT.class,
        MRSObservationAdapterImplIT.class, MRSPatientAdapterImplIT.class, MRSUserAdapterImplIT.class })
public class IntegrationSuite {
}
