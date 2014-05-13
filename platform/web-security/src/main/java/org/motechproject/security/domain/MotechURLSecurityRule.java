package org.motechproject.security.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.motechproject.security.constants.HTTPMethod;
import org.motechproject.security.constants.Protocol;
import org.motechproject.security.constants.Scheme;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

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
public class MotechURLSecurityRule implements Serializable {
    private static final long serialVersionUID = 1L;

    private String pattern;
    private List<Scheme> supportedSchemes;
    private Protocol protocol;
    private List<String> permissionAccess;
    private List<String> userAccess;
    private int priority;
    private boolean rest;
    private String origin;
    private String version;
    private Set<HTTPMethod> methodsRequired;
    private boolean active;
    private boolean deleted;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public List<Scheme> getSupportedSchemes() {
        return supportedSchemes;
    }

    public void setSupportedSchemes(List<Scheme> supportedSchemes) {
        this.supportedSchemes = supportedSchemes;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
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

    public Set<HTTPMethod> getMethodsRequired() {
        return methodsRequired;
    }

    public void setMethodsRequired(Set<HTTPMethod> methodsRequired) {
        this.methodsRequired = methodsRequired;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MotechURLSecurityRule that = (MotechURLSecurityRule) o;

        return new EqualsBuilder()
                .append(rest, that.rest)
                .append(methodsRequired, that.methodsRequired)
                .append(pattern, that.pattern)
                .append(protocol, that.protocol)
                .append(supportedSchemes, that.supportedSchemes)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(rest)
                .append(methodsRequired)
                .append(pattern)
                .append(protocol)
                .append(supportedSchemes)
                .toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
