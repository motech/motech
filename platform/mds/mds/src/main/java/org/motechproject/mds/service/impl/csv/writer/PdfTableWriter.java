package org.motechproject.mds.service.impl.csv.writer;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.motechproject.mds.exception.csv.DataExportException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An implementation of the table writer that writes the table data in PDF format.
 * Uses the iText PDF library underneath.
 */
public class PdfTableWriter implements TableWriter {

    private static final String ROW_NUMBER_HEADER = "No";
    private static final float MARGIN = 36f;
    private static final float PAGE_HEIGHT = PageSize.A4.getWidth() - 2 * MARGIN;
    private static final float PAGE_WIDTH = PageSize.A4.getHeight() - 2 * MARGIN;

    private final PdfWriter pdfWriter;
    private final Document pdfDocument;
    private final PdfContentByte pdfCanvas;
    private PdfPTable dataTable;
    private Map<String, Float> columnsWidths;
    private int rows = 0;
    private float tableContentOffset;

    public PdfTableWriter(OutputStream outputStream) {
        pdfDocument = new Document(new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth()));
        try {
            pdfWriter = PdfWriter.getInstance(pdfDocument, outputStream);
        } catch (DocumentException e) {
            throw new DataExportException("Unable to create a PDF writer instance", e);
        }
        pdfDocument.open();
        pdfCanvas = pdfWriter.getDirectContent();
    }

    @Override
    public void writeRow(Map<String, String> row, String[] headers) throws IOException {

        if (dataTable == null) {
            writeHeader(headers);
        }

        writeCell(ROW_NUMBER_HEADER, Integer.toString(rows++));

        for (String header : headers) {
            writeCell(header, row.get(header));
        }
    }

    @Override
    public void writeHeader(String[] headers) throws IOException {

        dataTable = new PdfPTable(headers.length + 1);
        columnsWidths = new LinkedHashMap<>();

        writeHeaderCell(ROW_NUMBER_HEADER);

        for (String header : headers) {
            writeHeaderCell(header);
        }
    }

    @Override
    public void close() {
        try {
            float[] relativeWidths = getRelativeWidths();

            List<Integer> lastColumnsForPages = calculateLastColumnsForPages(relativeWidths);
            resizeColumns(relativeWidths, lastColumnsForPages);
            setTableContentOffset(relativeWidths[0]);

            dataTable.setWidths(relativeWidths);
            dataTable.setLockedWidth(true);
            dataTable.setTotalWidth(calculateTotalTableWidth(relativeWidths));

            writeTable(lastColumnsForPages);

            pdfDocument.close();
        } catch (DocumentException e) {
            throw new DataExportException("Unable to add a table to the PDF file", e);
        } finally {
            pdfWriter.close();
        }
    }

    private void writeTable(List<Integer> lastColumnsForPages) {

        //1 is the index of first non-header row
        int currentRow = 1;

        do {
            currentRow = writePages(lastColumnsForPages, currentRow);
        } while (tableHasMoreRows(currentRow));
    }

    private int writePages(List<Integer> lastColumnsForPages, int firstRow) {

        int lastRow = writePageCellByCell(lastColumnsForPages.get(0), lastColumnsForPages.get(1), firstRow);

        for (int i = 2; i < lastColumnsForPages.size(); i++) {
            writePage(lastColumnsForPages.get(i - 1), lastColumnsForPages.get(i), firstRow, lastRow);
        }

        return lastRow;
    }

    private int writePageCellByCell(int firstColumn, int lastColumn, int firstRow) {
        float y = writeHeaders(firstColumn, lastColumn);
        int currentRow = firstRow;
        do {
            if (!tableHasMoreRows(currentRow)) {
                break;
            }
            writeIndexCells(currentRow, currentRow + 1, y);
            y = dataTable.writeSelectedRows(firstColumn, lastColumn, currentRow, currentRow + 1, tableContentOffset, y, pdfCanvas);
            currentRow++;
        } while (nextRowFitsOnCurrentPage(y, currentRow));
        pdfDocument.newPage();
        return currentRow;
    }

    private void writePage(int firstColumn, int lastColumn, int firstRow, int lastRow) {
        float y = writeHeaders(firstColumn, lastColumn);
        writeIndexCells(firstRow, lastRow, y);
        dataTable.writeSelectedRows(firstColumn, lastColumn, firstRow, lastRow, tableContentOffset, y, pdfCanvas);
        pdfDocument.newPage();
    }

    private Float writeHeaders(Integer from, Integer to) {
        dataTable.writeSelectedRows(0, 1, 0, 1, MARGIN, PAGE_HEIGHT + MARGIN, pdfCanvas);
        return dataTable.writeSelectedRows(from, to, 0, 1, tableContentOffset, PAGE_HEIGHT + MARGIN, pdfCanvas);
    }

    private void writeIndexCells(int firstRow, int lastRow, float y) {
        dataTable.writeSelectedRows(0, 1, firstRow, lastRow, MARGIN, y, pdfCanvas);
    }

    private void writeCell(String column, String value) {
        // we want blank cells to display, even if they are the only ones
        Chunk chunk = StringUtils.isBlank(value) ? Chunk.NEWLINE : new Chunk(value);
        // add as a cell to the table
        PdfPCell cell = new PdfPCell(new Phrase(chunk));
        dataTable.addCell(cell);
        updateWidthIfNeeded(column, cell);
    }

    private void writeHeaderCell(String column) {
        PdfPCell cell = new PdfPCell(new Phrase(column));
        cell.setBackgroundColor(BaseColor.GRAY);
        dataTable.addCell(cell);
        columnsWidths.put(column, calculateTotalCellWidth(cell));
    }

    private void resizeColumns(float[] relativeWidths, List<Integer> lastColumnsForPages) {
        for (int page = 1; page < lastColumnsForPages.size(); page++) {
            scaleColumnsToPageSize(relativeWidths, lastColumnsForPages.get(page - 1), lastColumnsForPages.get(page));
        }
    }

    private void scaleColumnsToPageSize(float[] relativeWidths, int firstColumn, int lastColumn) {

        float totalColumnsWidth = calculateTotalColumnsWidth(relativeWidths, firstColumn, lastColumn);

        for (int columnId = firstColumn; columnId < lastColumn; columnId++) {
            relativeWidths[columnId] = (PAGE_WIDTH - relativeWidths[0]) * (relativeWidths[columnId] / totalColumnsWidth);
        }
    }

    private float calculateTotalTableWidth(float[] widths) {
        float totalWidth = 0;
        for (float width : widths) {
            totalWidth += width;
        }
        return totalWidth;
    }

    private float calculateTotalColumnsWidth(float[] relativeWidths, int firstColumn, int lastColumn) {
        float totalColumnsSize = 0;

        for (int column = firstColumn; column < lastColumn; column++) {
            totalColumnsSize += relativeWidths[column];
        }

        return totalColumnsSize;
    }

    private List<Integer> calculateLastColumnsForPages(float[] relativeWidths) {
        List<Integer> lastColumnsForPages = new ArrayList<>();
        lastColumnsForPages.add(1);

        float totalColumnsWidth = 0;

        for (int i = 1; i < relativeWidths.length; i++) {
            if (totalColumnsWidth + relativeWidths[i] > PAGE_WIDTH - relativeWidths[0]) {
                lastColumnsForPages.add(i);
                totalColumnsWidth = 0;
            }
            totalColumnsWidth += relativeWidths[i];
        }

        lastColumnsForPages.add(-1);
        return lastColumnsForPages;
    }

    private Float calculateTotalCellWidth(PdfPCell cell) {
        return cell.getBorderWidthLeft()
                + cell.getEffectivePaddingLeft()
                + ColumnText.getWidth(cell.getPhrase())
                + cell.getEffectivePaddingRight()
                + cell.getBorderWidthRight();
    }

    private void updateWidthIfNeeded(String header, PdfPCell cell) {
        Float width = calculateTotalCellWidth(cell);

        if (columnsWidths.get(header) < width) {
            columnsWidths.put(header, width > PAGE_WIDTH ? PAGE_WIDTH : width);
        }
    }

    private boolean tableHasMoreRows(int rowNumber) {
        return rowNumber < dataTable.getRows().size();
    }

    private boolean nextRowFitsOnCurrentPage(float y, int currentRow) {
        return y - dataTable.getRowHeight(currentRow + 1) > MARGIN;
    }

    private void setTableContentOffset(float width) {
        tableContentOffset = MARGIN + width;
    }

    private float[] getRelativeWidths() {
        return ArrayUtils.toPrimitive(columnsWidths.values().toArray(new Float[0]));
    }
}
