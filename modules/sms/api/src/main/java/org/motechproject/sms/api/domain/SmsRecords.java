package org.motechproject.sms.api.domain;

import java.util.ArrayList;
import java.util.List;

public class SmsRecords {

    private int count;
    private List<SmsRecord> records;

    public SmsRecords() {
        this.count = 0;
        this.records = new ArrayList<>();
    }

    public SmsRecords(int count, List<SmsRecord> records) {
        this.count = count;
        this.records = records;
    }

    public int getCount() {
        return count;
    }

    public List<SmsRecord> getRecords() {
        return records;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setRecords(List<SmsRecord> records) {
        this.records = records;
    }
}
