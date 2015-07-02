package org.motechproject.mds.service;

/**
 * The <code>CsvExportCustomizer</code> interface allows to provide custom method to format related instances
 * during csv import.
 */
public interface CsvExportCustomizer {

    /**
     * Formats related instance into a csv value
     *
     * @param object the related instance (or collection of instances)
     *
     * @return formatted string
     */
    String formatRelationship(Object object);

}
