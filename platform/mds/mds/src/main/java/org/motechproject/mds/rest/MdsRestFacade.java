package org.motechproject.mds.rest;

import org.motechproject.mds.exception.rest.RestLookupExecutionForbiddenException;
import org.motechproject.mds.query.QueryParams;

import java.io.InputStream;
import java.util.Map;

/**
 * Interface called by the REST controller REST operations.
 * Should be exposed as an OSGi service for each MDS Entity.
 * If rest is not supported, it throws a {@link org.motechproject.mds.exception.rest.RestNotSupportedException}.
 *
 * @param <T> the entity class.
 */
public interface MdsRestFacade<T> {

    /**
     * Retrieves entity instances for REST. This will only include fields that are visible for REST.
     * It throws {@link org.motechproject.mds.exception.rest.RestOperationNotSupportedException} if the entity settings
     * do not permit READ access via REST.
     *
     * @param queryParams query parameters to use retrieving instances
     * @param includeBlob set to true, if you wish to retrieve value for binary object fields
     * @return a response that contains metadata and list of instances, in form of a map with field names and their
     * respective values
     */
    RestResponse get(QueryParams queryParams, boolean includeBlob);

    /**
     * Retrieves a single instance for REST. This will only include fields that are visible for REST.
     * It throws {@link org.motechproject.mds.exception.rest.RestOperationNotSupportedException} if the entity settings
     * do not permit READ access via REST.
     *
     * @param id id of the instance
     * @param includeBlob set to true, if you wish to retrieve value for binary object fields
     * @return  a response that contains metadata and instance
     */
    RestResponse get(Long id, boolean includeBlob);

    /**
     * Creates an instance in MDS, reading it from the input stream. Only fields that are visible via REST
     * will be set in the created instance. It will fail, if data read from input stream contains fields
     * that do not exists or if field values do not match their type. It also throws
     * {@link org.motechproject.mds.exception.rest.RestOperationNotSupportedException} if the entity settings
     * do not permit CREATE access via REST.
     *
     * @param instanceBody input stream, containing instance representation in JSON
     * @return created instance, in form of a map with field names and their respective values
     */
    RestProjection create(InputStream instanceBody);

    /**
     * Updates an instance in MDS, reading it from the input stream. Only fields that are visible via REST
     * will be set in the updated instance. It will fail, if data read from input stream contains fields
     * that do not exists or if field values do not match their type. It also throws
     * {@link org.motechproject.mds.exception.rest.RestOperationNotSupportedException} if the entity settings
     * do not permit UPDATE access via REST.
     *
     * @param instanceBody input stream, containing instance representation in JSON
     * @return updated instance, in form of a map with field names and their respective values
     */
    RestProjection update(InputStream instanceBody);

    /**
     * Deletes an instance by id. This works exactly like deleting an instance in any other way,
     * but will throw {@link org.motechproject.mds.exception.rest.RestOperationNotSupportedException} if
     * the entity settings do not permit DELETE access via REST.
     *
     * @param id id of the instance
     */
    void delete(Long id);

    /**
     * Executes a lookup for REST, given the lookup name, lookup parameters and query parameters. The
     * result will only contain fields that are visible for REST. If requested lookup is not available
     * via REST, this will throw {@link RestLookupExecutionForbiddenException}. If
     * a lookup of given name does not exist, it throws {@link org.motechproject.mds.exception.rest.RestLookupNotFoundException}.
     *
     * @param lookupName name of the lookup
     * @param lookupMap map containing field names and their respective values
     * @param queryParams query parameters to use retrieving instances
     * @param includeBlob set to true, if you wish to retrieve value for binary object fields
     * @return lookup result, that can be either a single instance or a collection of instances. Response contains also metadata.
     */
    Object executeLookup(String lookupName, Map<String, String> lookupMap, QueryParams queryParams, boolean includeBlob);
}
