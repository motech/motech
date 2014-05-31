package org.motechproject.batch.model;

import java.util.List;


/**
 * Class containing list of <code>BatchJobDTO</code> to be sent across in response
 * @author Naveen
 *
 */
public class BatchJobListDTO {
	private List<BatchJobDTO> batchJobDtoList;

	public List<BatchJobDTO> getBatchJobDtoList() {
		return batchJobDtoList;
	}

	public void setBatchJobDtoList(List<BatchJobDTO> batchJobDtoList) {
		this.batchJobDtoList = batchJobDtoList;
	}

}
