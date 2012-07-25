package org.motechproject.openmrs.atomfeed;

public interface OpenMrsHttpClient {

    String getOpenMrsAtomFeed();

    String getOpenMrsAtomFeedSinceDate(String lastUpdateTime);

}
