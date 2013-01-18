package org.motechproject.decisiontree.server.service.impl;

import org.motechproject.decisiontree.core.CallDetail;
import org.motechproject.decisiontree.server.repository.AllCallDetailRecords;
import org.motechproject.decisiontree.server.service.CalllogSearchParameters;
import org.motechproject.decisiontree.server.service.CalllogSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("calllogSearchService")
public class CalllogSearchServiceImpl implements CalllogSearchService {
    private AllCallDetailRecords allCallDetailRecords;

    @Autowired
    public CalllogSearchServiceImpl(AllCallDetailRecords allCallDetailRecords) {
        this.allCallDetailRecords = allCallDetailRecords;
    }


    @Override
    public List<CallDetail> search(CalllogSearchParameters searchParameters) {
        List<CallDetail> callLogs = allCallDetailRecords.search(searchParameters.getPhoneNumber(),
                searchParameters.getStartTime(),
                searchParameters.getEndTime(),
                searchParameters.getMinDuration(),
                searchParameters.getMaxDuration());

        return callLogs;
    }

}
