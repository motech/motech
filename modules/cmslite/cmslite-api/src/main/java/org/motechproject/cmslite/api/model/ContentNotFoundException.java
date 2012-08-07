package org.motechproject.cmslite.api.model;

/**
 * \ingroup cmslite
 * Thrown when content is not available.
 */
public class ContentNotFoundException extends Exception {

    public ContentNotFoundException() {
        super("Content with specified Name and Language does not exist");
    }
}
