package org.motechproject.testing.osgi;

public class MavenArtifact {

    private String groupId;
    private String artifactId;
    private String type;
    private String version;
    private String scope;

    /**
     * The string value of this object, cached so it does not need to be continuously recalculated.
     */
    private transient String stringValue;

    /**
     * Parse a maven artifact specification as output by the Maven dependency plugin resolve goal.
     *
     * @param spec the specification string formatted as groupId:artifactId:type:version:scope:other.
     * @return the artifact.
     * @throws IllegalArgumentException if the specification string does not contain the required parts with colon separators.
     */
    public static MavenArtifact parse(final String spec) {
        String[] parts = spec.trim().split(":");
        // CHECKSTYLE:OFF the number four is not magic.
        if (parts.length < 4) {
            // CHECKSTYLE:ON
            throw new IllegalArgumentException(
                    "The specification must contain at least 5 parts separated by a colon (:)."
                            + " The parts are: groupId:artifactId:type:version:scope:other");
        }
        MavenArtifact artifact = new MavenArtifact();
        if (parts[0].length() > 0) {
            artifact.groupId = parts[0].replaceAll(".* = ", "");
        }
        if (parts[1].length() > 0) {
            artifact.artifactId = parts[1];
        }
        if (parts[2].length() > 0) {
            artifact.type = parts[2];
        }
        // CHECKSTYLE:OFF the numbers three and four are not magic.
        if (parts[3].length() > 0) {
            artifact.version = parts[3];
        }
        if (parts.length >= 4 && parts[4].length() > 0) {
            artifact.scope = parts[4];
        }
        // CHECKSTYLE:ON
        return artifact;
    }

    /**
     * Output the values separated by colons exactly as the
     * parse method would like to see them. Null values are
     * output as the empty string.
     *
     * @return the string representation of this object.
     */
    @Override
    public String toString() {
        if (stringValue == null) {
            stringValue = asString(groupId) + ":" + asString(artifactId) + ":"
                    + asString(type) + ":" + asString(version)
                    + ":" + asString(scope);
        }
        return stringValue;
    }

    /**
     * Return "" if the argument is null.
     *
     * @param s the string to output.
     * @return "" if s is null, or s otherwise.
     */
    private String asString(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    /**
     * Check if the other object is an instance of this
     * class and that all its parts equal the parts in
     * this instance.
     *
     * @param other the object to test against this object.
     * @return true if the other object is an instance of
     *         this class and all its parts equals the parts
     *         of this class.
     */
    @Override
    public boolean equals(final Object other) {
        if ((other == null) || !(other instanceof MavenArtifact)) {
            return false;
        }
        MavenArtifact o = (MavenArtifact) other;
        return (isEqual(this.groupId, o.getGroupId())
                && isEqual(this.artifactId, o.getArtifactId())
                && isEqual(this.type, o.getType())
                && isEqual(this.version, o.getVersion())
                && isEqual(this.scope, o.getScope()));
    }

    /**
     * Output the hash code for this object.
     *
     * @return the hashCode calculated as the hashCode of
     *         the string returned by the toString method.
     * @see #toString()
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Both strings are null or they are equal.
     *
     * @param a one string.
     * @param b the other string.
     * @return true if both are null, or both are equal.
     */
    private boolean isEqual(final String a, final String b) {
        if (a == null) {
            return b == null;
        }
        if (b == null) {
            return false;
        }
        return a.equals(b);
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    public String getScope() {
        return scope;
    }
}
