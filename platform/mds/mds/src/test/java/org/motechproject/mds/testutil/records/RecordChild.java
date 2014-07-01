package org.motechproject.mds.testutil.records;

import org.joda.time.DateTime;

public class RecordChild extends Record {

    private DateTime newField;

    public DateTime getNewField() {
        return newField;
    }

    public void setNewField(DateTime newField) {
        this.newField = newField;
    }
}
