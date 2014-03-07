package org.motechproject.security.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * The MotechURLSecurityRule specifies the configuration
 * for setting up a Spring SecurityFilterChain. See the
 * SecurityConfigConstants class for valid values for
 * supportedSchemes, protocol and methodsRequired
 *
 * Details regarding configuration:
 *
 * pattern: URL pattern the security rule applies to
 * supportedSchemes: Security rules that should apply to the URL, such as BASIC or OPEN_ID
 *
 * protocol: Protocol the security rule applies to, such as HTTP or HTTPS
 *
 * permissionAccess: Requires user has at least one of the listed permission to access the URL
 *
 * userAccess: User specific access for a URL, such as motech or
 * admin, when combined with permission access they act as an either or (one must be true)
 *
 * priority: For future use in determining the ordering of filter chains, may be deprecated
 * depending on UI implementation
 *
 * rest: Whether the endpoint is meant for a form login process or as an REST end-point
 * that does not create a session for the
 *
 * origin: The module or user the rule originated from
 *
 * version: The version of the module or platform the rule was created
 *
 * methodsRequired: HTTP methods the rule applies to, if ANY is used then
 * any method is matched, if a set is used, such as GET, POST, etc, then
 * each will have its own corresponding filter chain with the same security found in that rule
 */
public class MotechURLSecurityRule implements Serializable {

    private static final long serialVersionUID = 1L;
    private String pattern;
    private List<String> supportedSchemes;
    private String protocol;
    private List<String> permissionAccess;
    private List<String> userAccess;
    private int priority;
    private boolean rest;
    private String origin;
    private String version;
    private Set<String> methodsRequired;
    private boolean active;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public List<String> getSupportedSchemes() {
        return supportedSchemes;
    }

    public void setSupportedSchemes(List<String> supportedSchemes) {
        this.supportedSchemes = supportedSchemes;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public List<String> getPermissionAccess() {
        return permissionAccess;
    }

    public void setPermissionAccess(List<String> permissionAccess) {
        this.permissionAccess = permissionAccess;
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

    public boolean isRest() {
        return rest;
    }

    public void setRest(boolean rest) {
        this.rest = rest;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Set<String> getMethodsRequired() {
        return methodsRequired;
    }

    public void setMethodsRequired(Set<String> methodsRequired) {
        this.methodsRequired = methodsRequired;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "MotechURLSecurityRule{" +
                "pattern='" + pattern + '\'' +
                ", supportedSchemes=" + supportedSchemes +
                ", protocol='" + protocol + '\'' +
                ", permissionAccess=" + permissionAccess +
                ", userAccess=" + userAccess +
                ", priority=" + priority +
                ", rest=" + rest +
                ", origin='" + origin + '\'' +
                ", version='" + version + '\'' +
                ", methodsRequired=" + methodsRequired +
                ", active=" + active +
                '}';
    }
}
