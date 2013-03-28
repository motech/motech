package org.motechproject.cmslite.api.model;

/**
 * \ingroup cmslite
 * Thrown when content is not available.
 */
public class ContentNotFoundException extends Exception {
    private static final long serialVersionUID = 3201333998658157310L;

    public ContentNotFoundException() {
        super("Content with specified Name and Language does not exist");
    }
}
