package org.motechproject.mds.testutil.records.history;

public class Record__History {
    private Long id;
    private Long record__HistoryCurrentVersion;
    private Boolean record__HistoryFromTrash;
    private String value;
    private Record__History record__HistoryNext;
    private Record__History record__HistoryPrevious;

    public Record__History() {
        this(null, null);
    }

    public Record__History(Long record__HistoryCurrentVersion, String value) {
        this.record__HistoryCurrentVersion = record__HistoryCurrentVersion;
        this.value = value;
        this.record__HistoryFromTrash = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecord__HistoryCurrentVersion() {
        return record__HistoryCurrentVersion;
    }

    public void setRecord__HistoryCurrentVersion(Long record__HistoryCurrentVersion) {
        this.record__HistoryCurrentVersion = record__HistoryCurrentVersion;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Record__History getRecord__HistoryNext() {
        return record__HistoryNext;
    }

    public void setRecord__HistoryNext(Record__History record__HistoryNext) {
        this.record__HistoryNext = record__HistoryNext;
    }

    public Record__History getRecord__HistoryPrevious() {
        return record__HistoryPrevious;
    }

    public void setRecord__HistoryPrevious(Record__History record__HistoryPrevious) {
        this.record__HistoryPrevious = record__HistoryPrevious;
    }

    public Boolean getRecord__HistoryFromTrash() {
        return record__HistoryFromTrash;
    }

    public void setRecord__HistoryFromTrash(Boolean record__HistoryFromTrash) {
        this.record__HistoryFromTrash = record__HistoryFromTrash;
    }
}
