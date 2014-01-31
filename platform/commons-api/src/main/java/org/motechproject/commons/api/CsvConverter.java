package org.motechproject.commons.api;

import java.util.List;

/**
 * The <code>CsvConverter</code> class, provides methods responsible for conversion to CSV-formatted strings.
 */

public final class CsvConverter {
    private static String csvString = "";
    private static final String SEPARATOR = ",";
    private static final String END_OF_LINE = "\r\n";

    private CsvConverter() {
        // static utility class
    }

    public static String convertToCSV(List<List<String>> list) {
        csvString = "";
        for (List<String> line : list) {
            for (String word : line) {
                String csvObject = word;
                if (csvObject.contains(SEPARATOR) || csvObject.contains("\"") || csvObject.contains(END_OF_LINE)) {
                    csvObject = csvObject.replace("\"", "\"\"");
                    csvObject = "\"" + csvObject + "\"";
                }
                csvString = csvString.concat(csvObject + SEPARATOR);
            }

            //removing last, unnecessary separator
            csvString = csvString.substring(0, csvString.lastIndexOf(SEPARATOR));
            csvString = csvString.concat(END_OF_LINE);
        }

        return csvString;
    }
}
