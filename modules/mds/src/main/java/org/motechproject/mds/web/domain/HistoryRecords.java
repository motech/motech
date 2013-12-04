package org.motechproject.mds.web.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Class required for jqGrid to exchange data with server
 */
public class HistoryRecords {
    private Integer page;
    private Integer total;
    private Integer records;
    private List<HistoryRecord> rows;

    public HistoryRecords(Integer page, Integer rows, List<HistoryRecord> historyRecords) {
        if (page != null && rows != null) {
            this.page = page;
            records = historyRecords.size();
            this.total = (this.records <= rows) ? 1 : (this.records / rows) + 1;
            this.rows = new ArrayList<>(historyRecords.subList((page - 1) * rows, (page * rows > this.records ? this.records : page * rows)));
        } else {
            this.rows = historyRecords;
        }
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getRecords() {
        return records;
    }

    public void setRecords(Integer records) {
        this.records = records;
    }

    public List<HistoryRecord> getRows() {
        return rows;
    }

    public void setRows(List<HistoryRecord> rows) {
        this.rows = rows;
    }
}
