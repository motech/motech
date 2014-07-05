package org.motechproject.batch.service.impl;

import javax.batch.api.Batchlet;

public class TestBatchlet implements Batchlet {

    @Override
    public String process() {
        return "cool";
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

}
