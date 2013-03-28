package org.motechproject.cmslite.api.web;

import org.motechproject.cmslite.api.model.Content;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Collections.addAll;
import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;

public class ResourceDto implements Serializable {
    private static final long serialVersionUID = 6728665456455509425L;

    private final Set<String> languages = new TreeSet<>();
    private final String name;
    private final String type;

    public ResourceDto(Content content) {
        this.name = content.getName();
        this.languages.add(content.getLanguage());

        if (startsWithIgnoreCase(content.getType(), "string")) {
            type = "string";
        } else if (startsWithIgnoreCase(content.getType(), "stream")) {
            type = "stream";
        } else {
            type = null;
        }
    }

    public ResourceDto(String name, String type, String... languages) {
        this.name = name;
        this.type = type;
        addAll(this.languages, languages);
    }

    public void addLanguage(String language) {
        languages.add(language);
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(languages, name, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final ResourceDto other = (ResourceDto) obj;

        return Objects.equals(this.languages, other.languages) &&
                Objects.equals(this.name, other.name) &&
                Objects.equals(this.type, other.type);
    }

    @Override
    public String toString() {
        return String.format("ResourceDto{languages=%s, name='%s', type='%s'}", languages, name, type);
    }
}
