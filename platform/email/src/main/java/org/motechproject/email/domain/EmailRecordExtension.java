package org.motechproject.email.domain;

import org.motechproject.mds.annotations.EntityExtension;
import org.motechproject.mds.annotations.Field;

@EntityExtension
public class EmailRecordExtension extends EmailRecord {

    @Field
    private String testText;

    public void setTestText(String testText) {
        this.testText = testText;
    }

    public String getTestText() {
        return testText;
    }
}
