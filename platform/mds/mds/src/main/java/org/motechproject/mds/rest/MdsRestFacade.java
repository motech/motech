package org.motechproject.mds.rest;

import org.motechproject.mds.query.QueryParams;

import java.util.List;

public interface MdsRestFacade<T> {

    List<T> get(QueryParams queryParams);

    void create(T instance);
}
