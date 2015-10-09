package org.motechproject.security.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.security.constants.HTTPMethod;
import org.motechproject.security.constants.Protocol;
import org.motechproject.security.constants.Scheme;

import java.util.List;

/**
 * The MotechURLSecurityRule specifies the configuration for setting up a Spring
 * SecurityFilterChain.
 * <p/>
 * Details regarding configuration:
 * <ul>
 * <li><strong>pattern</strong> - URL pattern the security rule applies to</li>
 * <li><strong>supportedSchemes</strong> - Security rules that should apply to the URL, such as
 * BASIC or OPEN_ID</li>
 * <li><strong>protocol</strong> - Protocol the security rule applies to, such as HTTP or HTTPS</li>
 * <li><strong>permissionAccess</strong> - Requires user has at least one of the listed permission
 * to access the URL</li>
 * <li><strong>userAccess</strong> - User specific access for a URL, such as motech or admin, when
 * combined with permission access they act as an either or (one must be true)</li>
 * <li><strong>priority</strong> - For future use in determining the ordering of filter chains, may
 * be deprecated depending on UI implementation</li>
 * <li><strong>rest</strong> - Whether the endpoint is meant for a form login process or as an REST
 * end-point that does not create a session for the</li>
 * <li><strong>origin</strong> - The module or user the rule originated from</li>
 * <li><strong>version</strong> - The version of the module or platform the rule was created</li>
 * <li><strong>methodsRequired</strong> - HTTP methods the rule applies to, if ANY is used then any
 * method is matched, if a set is used, such as GET, POST, etc, then each will have its own
 * corresponding filter chain with the same security found in that rule</li>
 * </ul>
 */
@Entity(recordHistory = true)
public class MotechURLSecurityRule {

    @Field
    private Long id;

    @Field
    private List<Scheme> supportedSchemes;

    @Field
    private List<String> permissionAccess;

    @Field
    private List<String> userAccess;

    @Field
    private List<HTTPMethod> methodsRequired;

    @Field
    private Protocol protocol;

    @Field
    private String origin;

    @Field(required = true)
    private String pattern;

    @Field
    private String version;

    @Field
    private boolean active;

    @Field
    private boolean deleted;

    @Field
    private boolean rest;

    @Field
    private int priority;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Scheme> getSupportedSchemes() {
        return supportedSchemes;
    }

    public void setSupportedSchemes(List<Scheme> supportedSchemes) {
        this.supportedSchemes = supportedSchemes;
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

    public List<HTTPMethod> getMethodsRequired() {
        return methodsRequired;
    }

    public void setMethodsRequired(List<HTTPMethod> methodsRequired) {
        this.methodsRequired = methodsRequired;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "MotechURLSecurityRule{" +
                "supportedSchemes=" + supportedSchemes +
                ", permissionAccess=" + permissionAccess +
                ", userAccess=" + userAccess +
                ", methodsRequired=" + methodsRequired +
                ", protocol=" + protocol +
                ", pattern='" + pattern + '\'' +
                '}';
    }
}
