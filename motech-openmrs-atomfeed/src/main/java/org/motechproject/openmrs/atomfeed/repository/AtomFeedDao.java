package org.motechproject.openmrs.atomfeed.repository;

public interface AtomFeedDao {

    void setLastUpdateTime(String id, String lastUpdateTime);

    String getLastUpdateTime();

    String getLastId();

}
