package org.motechproject.batch.web.dto;

import java.util.List;

public class BatchJobListDTO {

	List<BatchJobDTO> batchJobDTOList;

	public List<BatchJobDTO> getBatchJobDTOList() {
		return batchJobDTOList;
	}

	public void setBatchJobDTOList(List<BatchJobDTO> batchJobDTOList) {
		this.batchJobDTOList = batchJobDTOList;
	}
}
