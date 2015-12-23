package org.motechproject.mds.builder;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;

public class SampleWithIncrementStrategy {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    private String sampleText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSampleText() {
        return sampleText;
    }

    public void setSampleText(String sampleText) {
        this.sampleText = sampleText;
    }
}
