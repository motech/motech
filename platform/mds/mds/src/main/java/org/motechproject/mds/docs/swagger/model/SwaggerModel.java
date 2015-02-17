package org.motechproject.mds.docs.swagger.model;

import org.springframework.http.HttpMethod;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This represents the swagger documentation JSON file. This model is the single class that will be
 * serialized to JSON using GSON.
 * @see <a href="https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md">Swagger Spec specification</a>
 */
public class SwaggerModel implements Serializable {

    private static final long serialVersionUID = -9138283813584460539L;

    private String swagger;
    private Info info;
    private String host;
    private String basePath;

    private List<String> schemes;
    private List<String> consumes;
    private List<String> produces;

    private Map<String, Map<String, PathEntry>> paths;

    private Map<String, Definition> definitions;

    /**
     * @return the version of swagger
     */
    public String getSwagger() {
        return swagger;
    }

    /**
     * @param swagger the version of swagger
     */
    public void setSwagger(String swagger) {
        this.swagger = swagger;
    }

    /**
     * @return the info section from the spec
     * @see org.motechproject.mds.docs.swagger.model.Info
     */
    public Info getInfo() {
        return info;
    }

    /**
     * @param info the info section from the spec
     * @see org.motechproject.mds.docs.swagger.model.Info
     */
    public void setInfo(Info info) {
        this.info = info;
    }

    /**
     * @return the host to be used when executing calls from the UI
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to be used when executing calls from the UI
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the base path of the API to be appended after the hostname
     */
    public String getBasePath() {
        return basePath;
    }

    /**
     * @param basePath the base path of the API to be appended after the hostname
     */
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    /**
     * @return the schema used by this API, such as HTTP
     */
    public List<String> getSchemes() {
        return schemes;
    }

    /**
     * @param schemes the schema used by this API, such as HTTP
     */
    public void setSchemes(List<String> schemes) {
        this.schemes = schemes;
    }

    /**
     * @return mime types consumed by this API(format of the body parameters coming in)
     */
    public List<String> getConsumes() {
        return consumes;
    }

    /**
     * @param consumes mime types consumed by this API(format of the body parameters coming in)
     */
    public void setConsumes(List<String> consumes) {
        this.consumes = consumes;
    }

    /**
     * @return mime types produced by this API(format of items returned in responses)
     */
    public List<String> getProduces() {
        return produces;
    }

    /**
     * @param produces mime types produced by this API(format of items returned in responses)
     */
    public void setProduces(List<String> produces) {
        this.produces = produces;
    }

    /**
     * This are the paths of this API. Each path will have its own expandable widget on the UI that will allow
     * users to perform that particular operation. The keys in the first map are url endpoints(after the base path).
     * The keys in the inner map are methods used for access such as get, post, put, delete. This means each endpoint can
     * have multiple methods for accessing it.
     * @return the map of endpoints for this API
     * @see org.motechproject.mds.docs.swagger.model.PathEntry
     */
    public Map<String, Map<String, PathEntry>> getPaths() {
        return paths;
    }

    /**
     * This are the paths of this API. Each path will have its own expandable widget on the UI that will allow
     * users to perform that particular operation. The keys in the first map are url endpoints(after the base path).
     * The keys in the inner map are methods used for access such as get, post, put, delete. This means each endpoint can
     * have multiple methods for accessing it.
     * @param paths  the map of endpoints for this API
     * @see org.motechproject.mds.docs.swagger.model.PathEntry
     */
    public void setPaths(Map<String, Map<String, PathEntry>> paths) {
        this.paths = paths;
    }

    /**
     * The definitions in this API. Keys are names that can be used to reference the models.
     * These definitions will represent the more complex models exchanged with this API.
     * @return the map of definitions for this API
     * @see org.motechproject.mds.docs.swagger.model.Definition
     */
    public Map<String, Definition> getDefinitions() {
        return definitions;
    }

    /**
     * The definitions in this API. Keys are names that can be used to reference the models.
     * These definitions will represent the more complex models exchanged with this API.
     * @param definitions  the map of definitions for this API
     * @see org.motechproject.mds.docs.swagger.model.Definition
     */
    public void setDefinitions(Map<String, Definition> definitions) {
        this.definitions = definitions;
    }

    /**
     * Adds a path entry representing an operation to this API spec.
     * @param path the path under which this operation is accessible
     * @param httpMethod the http method used for accessing this operation
     * @param pathEntry the {@link org.motechproject.mds.docs.swagger.model.PathEntry} that describes this endpoint
     * @see org.motechproject.mds.docs.swagger.model.PathEntry
     */
    public void addPathEntry(String path, HttpMethod httpMethod, PathEntry pathEntry) {
        if (paths == null) {
            paths = new LinkedHashMap<>();
        }
        if (!paths.containsKey(path)) {
            paths.put(path, new LinkedHashMap<String, PathEntry>());
        }
        paths.get(path).put(httpMethod.name().toLowerCase(), pathEntry);
    }

    /**
     * Adds a definition to this API spec.
     * @param name the name of the definition
     * @param definition the definition which will describe the model under the given name
     * @see org.motechproject.mds.docs.swagger.model.Definition
     */
    public void addDefinition(String name, Definition definition) {
        if (definitions == null) {
            definitions = new LinkedHashMap<>();
        }
        definitions.put(name, definition);
    }
}
