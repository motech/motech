package org.motechproject.mds.service.impl.csv.writer;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.motechproject.mds.ex.csv.CsvExportException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * An implementation of the table writer that writes the table data in PDF format.
 * Uses the iText PDF library underneath.
 */
public class PdfTableWriter implements TableWriter {

    private final PdfWriter pdfWriter;
    private final Document pdfDocument;
    private PdfPTable dataTable;

    public PdfTableWriter(OutputStream outputStream) {
        pdfDocument = new Document(PageSize.A0);
        try {
            pdfWriter = PdfWriter.getInstance(pdfDocument, outputStream);
        } catch (DocumentException e) {
            throw new CsvExportException("Unable to create a PDF writer instance", e);
        }
        pdfDocument.open();
    }

    @Override
    public void writeRow(Map<String, String> row, String[] headers) throws IOException {
        if (dataTable == null) {
            writeHeader(headers);
        }

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(row.get(header)));
            dataTable.addCell(cell);
        }
    }

    @Override
    public void writeHeader(String[] headers) throws IOException {
        dataTable = new PdfPTable(headers.length);
        dataTable.setWidthPercentage(100);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            dataTable.addCell(cell);
        }
    }

    @Override
    public void close() {
        try {
            pdfDocument.add(dataTable);
            pdfDocument.close();
        } catch (DocumentException e) {
            throw new CsvExportException("Unable to add a table to the PDF file", e);
        } finally {
            pdfWriter.close();
        }
    }
}
