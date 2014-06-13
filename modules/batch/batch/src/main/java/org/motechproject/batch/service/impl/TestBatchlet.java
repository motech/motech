package org.motechproject.batch.service.impl;

import javax.batch.api.Batchlet;

public class TestBatchlet implements Batchlet{

	@Override
	public String process() throws Exception {
		System.out.println("processing the batch jpb");
		return "cool";
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
