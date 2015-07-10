package org.motechproject.mds.service.impl.csv.writer;

import java.io.IOException;
import java.util.Map;

/**
 * Created by GES0_000 on 2015-07-08.
 */
public interface TableWriter extends AutoCloseable {

    void writeRow(Map<String, String> row, String[] headers) throws IOException;

    void writeHeader(String[] headers) throws IOException;

    void close();
}
