package org.motechproject.commcare.service;

import org.motechproject.commcare.domain.CommcareFixture;

import java.util.List;

/**
 * A service to perform queries against CommCareHQ's fixture APIs.
 */
public interface CommcareFixtureService {

    /**
     * Queries CommCareHQ for a list of all fixtures on the configured domain.
     * @return A list of CommcareFixture that represent the information about each fixture from CommCareHQ
     */
    List<CommcareFixture> getAllFixtures();

    /**
     * Queries CommCareHQ for a specific fixture
     * @param id The id of the fixture to retrieve.
     * @return A CommcareFixture object representing the information about the fixture from CommCareHQ, or null if that fixture did not exist on that domain.
     */
    CommcareFixture getCommcareFixtureById(String id);
}
