package org.motechproject.mds.json.rest.swagger.model;

import org.springframework.http.HttpMethod;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This represents the swagger documentation JSON file.
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

    public String getSwagger() {
        return swagger;
    }

    public void setSwagger(String swagger) {
        this.swagger = swagger;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public List<String> getSchemes() {
        return schemes;
    }

    public void setSchemes(List<String> schemes) {
        this.schemes = schemes;
    }

    public List<String> getConsumes() {
        return consumes;
    }

    public void setConsumes(List<String> consumes) {
        this.consumes = consumes;
    }

    public List<String> getProduces() {
        return produces;
    }

    public void setProduces(List<String> produces) {
        this.produces = produces;
    }

    public Map<String, Map<String, PathEntry>> getPaths() {
        return paths;
    }

    public void setPaths(Map<String, Map<String, PathEntry>> paths) {
        this.paths = paths;
    }

    public Map<String, Definition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Map<String, Definition> definitions) {
        this.definitions = definitions;
    }

    public void addPathEntry(String path, HttpMethod httpMethod, PathEntry pathEntry) {
        if (!paths.containsKey(path)) {
            paths.put(path, new HashMap<String, PathEntry>());
        }
        paths.get(path).put(httpMethod.name().toLowerCase(), pathEntry);
    }
}
