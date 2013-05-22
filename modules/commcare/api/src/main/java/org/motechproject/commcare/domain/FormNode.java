package org.motechproject.commcare.domain;

public interface FormNode {
    String PREFIX_SEARCH_RELATIVE = "//";
    String PREFIX_SEARCH_FROM_ROOT = "/";
    String PREFIX_VALUE = "#";
    String PREFIX_ATTRIBUTE = "@";

    String getValue();
}
