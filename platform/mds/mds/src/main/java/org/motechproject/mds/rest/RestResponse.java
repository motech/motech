package org.motechproject.mds.rest;

import org.motechproject.mds.query.QueryParams;

import java.util.Arrays;
import java.util.List;

/**
 * The <code>RestResponse</code> class represents data retrieved over REST.
 * It contains metadata and data.
 *
 * @see org.motechproject.mds.rest.MdsRestFacade
 * @see org.motechproject.mds.rest.RestProjection
 * @see org.motechproject.mds.rest.RestMetadata
 */
public class RestResponse {

    private RestMetadata metadata;

    private List<RestProjection> data;

    /**
     * Default constructor.
     */
    public RestResponse() {
    }

    /**
     * Constructor.
     *
     * @param entity the entity name
     * @param className the name of the entity class
     * @param moduleName the module name
     * @param namespace the namespace in which the entity is defined
     * @param totalSize the total number of data that match the search conditions
     * @param queryParams the query params used to retrieve data
     * @param data the list of the data
     */
    public RestResponse(String entity, String className, String moduleName, String namespace, Long totalSize,
                        QueryParams queryParams, List<RestProjection> data) {
        this.metadata =  new RestMetadata(entity, className, moduleName, namespace, totalSize, queryParams);
        this.data = data;
    }

    /**
     * Constructor.
     *
     * @param entity the entity name
     * @param className the name of the entity class
     * @param moduleName the module name
     * @param namespace the namespace in which the entity is defined
     * @param totalSize the total number of data that match the search conditions
     * @param queryParams the query params used to retrieve data
     * @param data the record
     */
    public RestResponse(String entity, String className, String moduleName, String namespace, Long totalSize,
                        QueryParams queryParams, RestProjection data) {
        this.metadata =  new RestMetadata(entity, className, moduleName, namespace, totalSize, queryParams);
        this.data = Arrays.asList(data);
    }

    /**
     * @return the metadata for the response
     */
    public RestMetadata getMetadata() {
        return metadata;
    }

    /**
     * @param metadata the metadata for the response
     */
    public void setMetadata(RestMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * @return the list of the data
     */
    public List<RestProjection> getData() {
        return data;
    }

    /**
     * @param data the list of the data
     */
    public void setData(List<RestProjection> data) {
        this.data = data;
    }
}
