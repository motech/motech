package org.motechproject.mds.service.impl.csv.writer;

import org.apache.commons.io.IOUtils;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Created by GES0_000 on 2015-07-08.
 */
public class CsvTableWriter implements TableWriter {

    private final CsvMapWriter csvMapWriter;

    public CsvTableWriter(Writer writer) {
        csvMapWriter = new CsvMapWriter(writer, CsvPreference.STANDARD_PREFERENCE);
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(csvMapWriter);
    }

    @Override
    public void writeRow(Map<String, String> row, String[] headers) throws IOException {
        csvMapWriter.write(row, headers);
    }

    @Override
    public void writeHeader(String[] headers) throws IOException {
        csvMapWriter.writeHeader(headers);
    }
}
