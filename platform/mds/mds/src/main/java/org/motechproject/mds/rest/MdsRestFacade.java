package org.motechproject.mds.rest;

import org.motechproject.mds.query.QueryParams;

import java.io.InputStream;
import java.util.List;

public interface MdsRestFacade<T> {

    List<T> get(QueryParams queryParams);

    void create(InputStream instanceBody);

    void update(InputStream instanceBody);

    void delete(Long id);
}
