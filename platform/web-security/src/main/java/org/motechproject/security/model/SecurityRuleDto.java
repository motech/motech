package org.motechproject.security.model;

import java.util.List;

/**
 * Transfer Motech security rule data between representations.
 */
public class SecurityRuleDto {
    private Long id;
    private boolean active;
    private boolean deleted;
    private boolean rest;
    private List<String> methodsRequired;
    private List<String> permissionAccess;
    private List<String> supportedSchemes;
    private List<String> userAccess;
    private int priority;
    private String origin;
    private String pattern;
    private String protocol;
    private String version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isRest() {
        return rest;
    }

    public void setRest(boolean rest) {
        this.rest = rest;
    }

    public List<String> getMethodsRequired() {
        return methodsRequired;
    }

    public void setMethodsRequired(List<String> methodsRequired) {
        this.methodsRequired = methodsRequired;
    }

    public List<String> getPermissionAccess() {
        return permissionAccess;
    }

    public void setPermissionAccess(List<String> permissionAccess) {
        this.permissionAccess = permissionAccess;
    }

    public List<String> getSupportedSchemes() {
        return supportedSchemes;
    }

    public void setSupportedSchemes(List<String> supportedSchemes) {
        this.supportedSchemes = supportedSchemes;
    }

    public List<String> getUserAccess() {
        return userAccess;
    }

    public void setUserAccess(List<String> userAccess) {
        this.userAccess = userAccess;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
