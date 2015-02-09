package org.motechproject.mds.service;


import org.motechproject.mds.ex.action.ActionHandlerException;

import java.util.Map;

/**
 * The <code>ActionHandlerService</code> interface provides methods for handling tasks actions events related
 * with MDS CRUD operations.
 */
public interface ActionHandlerService {
    void create(Map<String, Object> parameters) throws ActionHandlerException;
    void update(Map<String, Object> parameters) throws ActionHandlerException;
    void delete(Map<String, Object> parameters) throws ActionHandlerException;
}
