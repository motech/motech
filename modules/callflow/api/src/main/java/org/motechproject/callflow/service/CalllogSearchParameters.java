package org.motechproject.callflow.service;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class CalllogSearchParameters {
    private static final String DEFAULT_DATE_FORMAT = "mm/dd/yyyy HH:mm";
    private String phoneNumber;

    private DateTime fromDate;
    private DateTime toDate;

    private Integer minDuration;
    private Integer maxDuration;


    private boolean answered;
    private boolean busy;
    private boolean failed;
    private boolean noAnswer;
    private boolean unknown;

    private int page;
    private String sortColumn;

    private boolean sortReverse;

    public void setSortReverse(boolean sortReverse) {
        this.sortReverse = sortReverse;
    }

    public boolean isSortReverse() {
        return sortReverse;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public String getToDate() {
        return toDate.toString(DEFAULT_DATE_FORMAT);
    }

    public DateTime getToDateAsDateTime() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate(toDate);
    }

    private DateTime toDate(String date) {
        return isNotBlank(date) ? DateTime.parse(date, DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT)) : null;
    }

    public String getFromDate() {
        return fromDate.toString(DEFAULT_DATE_FORMAT);
    }


    public DateTime getFromDateAsDateTime() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = toDate(fromDate);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(Integer maxDuration) {
        this.maxDuration = maxDuration;
    }

    public Integer getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(Integer minDuration) {
        this.minDuration = minDuration;
    }

    public boolean getAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public boolean getBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public boolean getFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public boolean getNoAnswer() {
        return noAnswer;
    }

    public void setNoAnswer(boolean noAnswer) {
        this.noAnswer = noAnswer;
    }

    public boolean getUnknown() {
        return unknown;
    }

    public void setUnknown(boolean unknown) {
        this.unknown = unknown;
    }
}

