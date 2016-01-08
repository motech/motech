package org.motechproject.mds.rest;

import org.motechproject.mds.query.QueryParams;

/**
 * The <code>RestResponse</code> class represents metadata of retrieved instances over REST.
 * It contains entity name, entity class name, module name, namespace and pagination information
 *
 * @see org.motechproject.mds.rest.MdsRestFacade
 * @see org.motechproject.mds.rest.RestProjection
 * @see org.motechproject.mds.rest.RestMetadata
 */
public class RestMetadata {

    private String entity;

    private String className;

    private String module;

    private String namespace;

    private long totalCount;

    private int page;

    private int pageSize;

    /**
     * Default constructor.
     */
    public RestMetadata() {
    }

    /**
     * Constructor.
     *
     * @param entity the entity name
     * @param className the name of the entity class
     * @param moduleName the module name
     * @param namespace the namespace in which the entity is defined
     * @param totalCount the total number of instances that match the search conditions
     * @param queryParams the query params used to retrieve instances
     */
    public RestMetadata(String entity, String className, String moduleName, String namespace, Long totalCount, QueryParams queryParams) {
        this.entity = entity;
        this.className = className;
        this.module = moduleName;
        this.namespace = namespace;
        this.totalCount = totalCount;
        this.page = queryParams.getPage();
        this.pageSize = queryParams.getPageSize();
    }

    /**
     * @return the entity name
     */
    public String getEntity() {
        return entity;
    }

    /**
     * @param entity the entity name
     */
    public void setEntity(String entity) {
        this.entity = entity;
    }

    /**
     * @return the class name of the entity
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the class name of the entity
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the module name
     */
    public String getModule() {
        return module;
    }

    /**
     * @param module the module name
     */
    public void setModule(String module) {
        this.module = module;
    }

    /**
     * @return the namespace in which the entity is defined
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @param namespace the namespace in which the entity is defined
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * @return the total count of instances that match the search conditions
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * @param totalCount the total count of instances that match the search conditions
     */
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return the page number
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page the page number
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return the page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the page size
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
