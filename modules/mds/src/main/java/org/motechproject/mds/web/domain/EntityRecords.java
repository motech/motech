package org.motechproject.mds.web.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Class required for jqGrid to exchange data with server
 */
public class EntityRecords {
    private Integer page;
    private Integer total;
    private Integer records;
    private List<EntityRecord> rows;

    public EntityRecords(Integer page, Integer rows, List<EntityRecord> entityRecords) {
        if (page != null && rows != null) {
            this.page = page;
            records = entityRecords.size();
            this.total = (this.records <= rows) ? 1 : (this.records / rows) + 1;
            this.rows = new ArrayList<>(entityRecords.subList((page - 1) * rows, (page * rows > this.records ? this.records : page * rows)));
        } else {
            this.rows = entityRecords;
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

    public List<EntityRecord> getRows() {
        return rows;
    }

    public void setRows(List<EntityRecord> rows) {
        this.rows = rows;
    }
}
