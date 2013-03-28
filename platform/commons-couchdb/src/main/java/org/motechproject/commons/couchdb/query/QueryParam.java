package org.motechproject.commons.couchdb.query;

public class QueryParam {

    private int pageNumber;
    private int recordsPerPage;
    private String sortBy;
    private boolean reverse;

    public QueryParam() {
        this.pageNumber = 0;
        this.recordsPerPage = 100;
        this.sortBy = null;
        this.reverse = false;
    }

    public QueryParam(int pageNumber, int recordsPerPage, String sortBy, boolean reverse) {
        this.pageNumber = pageNumber;
        this.recordsPerPage = recordsPerPage;
        this.sortBy = sortBy;
        this.reverse = reverse;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getRecordsPerPage() {
        return recordsPerPage;
    }

    public void setRecordsPerPage(int recordsPerPage) {
        this.recordsPerPage = recordsPerPage;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }
}
