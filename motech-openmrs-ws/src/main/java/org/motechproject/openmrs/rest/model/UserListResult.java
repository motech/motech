package org.motechproject.openmrs.rest.model;

import java.util.List;

public class UserListResult {

    private List<User> results;

    public List<User> getResults() {
        return results;
    }

    public void setResults(List<User> results) {
        this.results = results;
    }
}
