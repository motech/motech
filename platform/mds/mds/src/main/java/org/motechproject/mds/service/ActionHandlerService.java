package org.motechproject.mds.service;

import org.motechproject.mds.exception.action.ActionHandlerException;

import java.util.Map;

/**
 * The <code>ActionHandlerService</code> interface provides methods for handling tasks actions events related
 * with MDS CRUD operations.
 */
public interface ActionHandlerService {

    /**
     * Creates an instance of the entity, based on the provided parameters. The parameters should
     * contain the entity class name and field values.
     *
     * @param parameters a map of parameters
     * @throws ActionHandlerException if the instance of the entity could not get created due to missing class name,
     *                                lack of constructor, or any other reasons
     */
    void create(Map<String, Object> parameters) throws ActionHandlerException;

    /**
     * Updates an instance of the entity, based on the provided parameters. The parameters should
     * contain the entity class name, id of the instance and field values to update.
     *
     * @param parameters a map of parameters
     * @throws ActionHandlerException if the instance could not get updated due to missing class name, missing id of the instance,
     *                                missing instance of the given id, problems setting instance properties, or any other reasons
     */
    void update(Map<String, Object> parameters) throws ActionHandlerException;

    /**
     * Updates or creates an instance of the entity, based on the provided parameters.
     *
     * @param parameters a map of parameters
     * @throws ActionHandlerException if the instance could not get updated / created due to missing class name, missing id of the instance,
     *                                missing instance of the given id, problems setting instance properties, or any other reasons
     */
    void createOrUpdate(Map<String, Object> parameters) throws ActionHandlerException;

    /**
     * Query and updates each instance of the entity, based on the provided parameters.
     *
     * @param parameters
     * @throws ActionHandlerException if the instance could not get updated due to missing class name, missing id of the instance,
     *                                missing instance of the given id, problems setting instance properties, or any other reasons
     */
    void queryAndUpdate(Map<String, Object> parameters) throws ActionHandlerException;

    /**
     * Deletes an instance of the entity, based on the provided parameters. The parameters should contain
     * the entity class name and instance id.
     *
     * @param parameters a map of parameters
     * @throws ActionHandlerException if the instance could not get deleted due to missing class name, missing id of the instance,
     *                                missing instance of the given id, or any other reasons.
     */
    void delete(Map<String, Object> parameters) throws ActionHandlerException;
}
