package org.motechproject.cmslite.api.web;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;

public class GridSettings {
    private String languages = "";
    private String name = "";
    private String sortColumn = "";
    private String sortDirection = "asc";
    private Boolean stream = true;
    private Boolean string = true;
    private Integer page = 1;
    private Integer rows = 5;

    public boolean isCorrect(String contentName, String contentLanguage, String contentType) {
        boolean equalLanguage = isBlank(languages) || containsLanguage(contentLanguage);
        boolean equalName = isBlank(getName()) || startsWithIgnoreCase(contentName, getName());
        boolean equalString = isString() && equalsIgnoreCase(contentType, "string");
        boolean equalStream = isStream() && equalsIgnoreCase(contentType, "stream");

        return equalLanguage && equalName && (equalString || equalStream);
    }

    public boolean isDescending() {
        return equalsIgnoreCase(sortDirection, "desc");
    }

    public boolean containsLanguage(String language) {
        return asList(languages.split(",")).contains(language);
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        if (isNotBlank(languages)) {
            this.languages = languages;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (isNotBlank(name)) {
            this.name = name;
        }
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        if (isNotBlank(sortColumn)) {
            this.sortColumn = sortColumn;
        }
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        if (isNotBlank(sortDirection)) {
            this.sortDirection = sortDirection;
        }
    }

    public Boolean isStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        if (stream != null) {
            this.stream = stream;
        }
    }

    public Boolean isString() {
        return string;
    }

    public void setString(Boolean string) {
        if (string != null) {
            this.string = string;
        }
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        if (page != null) {
            this.page = page;
        }
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        if (rows != null) {
            this.rows = rows;
        }
    }

}
