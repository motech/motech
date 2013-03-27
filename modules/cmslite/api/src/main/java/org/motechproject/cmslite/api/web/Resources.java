package org.motechproject.cmslite.api.web;

import java.io.Serializable;
import java.util.List;

public class Resources implements Serializable {
    private static final long serialVersionUID = -6205245415683301270L;

    private final Integer page;
    private final Integer total;
    private final Integer records;
    private final List<ResourceDto> rows;

    public Resources(GridSettings settings, List<ResourceDto> list) {
        this.page = settings.getPage();
        records = list.size();
        total = records <= settings.getRows() ? 1 : (records / settings.getRows()) + 1;

        Integer start = settings.getRows() * (page > total ? total : page) - settings.getRows();
        Integer count = start + settings.getRows();
        Integer end = count > records ? records : count;

        this.rows = list.subList(start, end);
    }

    public Integer getPage() {
        return page;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getRecords() {
        return records;
    }

    public List<ResourceDto> getRows() {
        return rows;
    }

    @Override
    public String toString() {
        return String.format("Resources{page=%d, total=%d, records=%d, rows=%s}", page, total, records, rows);
    }
}
