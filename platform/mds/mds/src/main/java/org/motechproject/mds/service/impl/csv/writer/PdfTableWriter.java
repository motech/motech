package org.motechproject.mds.service.impl.csv.writer;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.motechproject.mds.ex.csv.DataExportException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An implementation of the table writer that writes the table data in PDF format.
 * Uses the iText PDF library underneath.
 */
public class PdfTableWriter implements TableWriter {

    private static final float MARGIN = 36f;
    private static final float MAX_COLUMN_WIDTH = 1500;

    private final PdfWriter pdfWriter;
    private final Document pdfDocument;
    private PdfPTable dataTable;
    private Map<String, Float> columnsWidths;

    public PdfTableWriter(OutputStream outputStream) {
        pdfDocument = new Document(PageSize.A0);
        try {
            pdfWriter = PdfWriter.getInstance(pdfDocument, outputStream);
        } catch (DocumentException e) {
            throw new DataExportException("Unable to create a PDF writer instance", e);
        }
        pdfDocument.open();
    }

    @Override
    public void writeRow(Map<String, String> row, String[] headers) throws IOException {
        if (dataTable == null) {
            writeHeader(headers);
        }

        for (String header : headers) {
            String value = row.get(header);
            // we want blank cells to display, even if they are the only ones
            Chunk chunk = StringUtils.isBlank(value) ? Chunk.NEWLINE : new Chunk(value);
            // add as a cell to the table
            PdfPCell cell = new PdfPCell(new Phrase(chunk));
            dataTable.addCell(cell);
            updateWidthIfNeeded(header, cell);
        }
    }

    @Override
    public void writeHeader(String[] headers) throws IOException {
        dataTable = new PdfPTable(headers.length);
        columnsWidths = new LinkedHashMap<>();

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(BaseColor.GRAY);
            dataTable.addCell(cell);
            columnsWidths.put(header, calculateCellWidth(cell));
        }
    }

    @Override
    public void close() {
        try {
            float[] relativeWidths = getRelativeWidths();
            float tableWidth = calculateTotalTableWidth(relativeWidths);

            dataTable.setWidths(relativeWidths);
            dataTable.setLockedWidth(true);
            dataTable.setTotalWidth(tableWidth);

            changeDocumentSize(tableWidth, dataTable.getTotalHeight());

            pdfDocument.add(dataTable);
            pdfDocument.close();
        } catch (DocumentException e) {
            throw new DataExportException("Unable to add a table to the PDF file", e);
        } finally {
            pdfWriter.close();
        }
    }

    private float calculateTotalTableWidth(float[] widths) {
        float totalWidth = 0;
        for (float width : widths) {
            totalWidth += width;
        }
        return totalWidth;
    }

    private void updateWidthIfNeeded(String header, PdfPCell cell) {
        Float width = calculateCellWidth(cell);

        if (columnsWidths.get(header) < width) {
            columnsWidths.put(header, width > MAX_COLUMN_WIDTH ? MAX_COLUMN_WIDTH : width);
        }
    }

    private Float calculateCellWidth(PdfPCell cell) {
        return cell.getBorderWidthLeft()
                + cell.getEffectivePaddingLeft()
                + ColumnText.getWidth(cell.getPhrase())
                + cell.getEffectivePaddingRight()
                + cell.getBorderWidthRight();
    }

    private float[] getRelativeWidths() {
        return ArrayUtils.toPrimitive(columnsWidths.values().toArray(new Float[0]));
    }

    //workaround for changing the size of the first page after document has been initiated
    private void changeDocumentSize(float tableWidth, float tableHeight) {
        float documentWidth = 2 * MARGIN + tableWidth;
        float documentHeight = 2 * MARGIN + tableHeight;
        pdfDocument.setPageSize(new Rectangle(documentWidth, documentHeight));
        pdfDocument.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
        pdfDocument.newPage();
    }
}
