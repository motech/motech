package org.motechproject.commons.couchdb.query;

/**
 * @deprecated As of release 0.24, MDS replaces CouchDB for persistence
 */
@Deprecated
public class QueryParam {

    private int pageNumber;
    private int recordsPerPage;
    private String sortBy;
    private boolean reverse;
    private static final int DEFAULT_RECORDS_PER_PAGE = 100;

    public QueryParam() {
        this.pageNumber = 0;
        this.recordsPerPage = DEFAULT_RECORDS_PER_PAGE;
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
