package org.motechproject.ivr.service;

import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.domain.CallDisposition;
import org.motechproject.ivr.domain.CallRecordSearchParameters;
import org.motechproject.ivr.repository.AllCallDetailRecords;
import org.motechproject.ivr.service.contract.CallRecordsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CallRecordsSearchServiceImpl implements CallRecordsSearchService {
    private AllCallDetailRecords allCallDetailRecords;
    private static final int PAGE_SIZE = 20;

    @Autowired
    public CallRecordsSearchServiceImpl(AllCallDetailRecords allCallDetailRecords) {
        this.allCallDetailRecords = allCallDetailRecords;
    }

    @Override
    public List<CallDetailRecord> search(CallRecordSearchParameters callLogSearchParameters) {

        return allCallDetailRecords.search(callLogSearchParameters.getPhoneNumber(),
                callLogSearchParameters.getFromDateAsDateTime(),
                callLogSearchParameters.getToDateAsDateTime(),
                callLogSearchParameters.getMinDuration(),
                callLogSearchParameters.getMaxDuration(),
                mapToDispositions(callLogSearchParameters), callLogSearchParameters.getPage(), PAGE_SIZE, callLogSearchParameters.getSortColumn(), callLogSearchParameters.isSortReverse());
    }

    @Override
    public long count(CallRecordSearchParameters callLogSearchParameters) {
        double numOfPages = allCallDetailRecords.countRecords(callLogSearchParameters.getPhoneNumber(),
                callLogSearchParameters.getFromDateAsDateTime(),
                callLogSearchParameters.getToDateAsDateTime(),
                callLogSearchParameters.getMinDuration(),
                callLogSearchParameters.getMaxDuration(), mapToDispositions(callLogSearchParameters)) / (PAGE_SIZE * 1.0);
        return Math.round(Math.ceil(numOfPages));
    }

    @Override
    public List<String> getAllPhoneNumbers() {
        return allCallDetailRecords.getAllPhoneNumbers();
    }

    @Override
    public long findMaxCallDuration() {
        return allCallDetailRecords.findMaxCallDuration();
    }

    private List<String> mapToDispositions(CallRecordSearchParameters callLogSearchParameters) {
        List<String> dispositions = new ArrayList<>();

        if (callLogSearchParameters.getAnswered()) {
            dispositions.add(CallDisposition.ANSWERED.name());
        }
        if (callLogSearchParameters.getBusy()) {
            dispositions.add(CallDisposition.BUSY.name());
        }
        if (callLogSearchParameters.getFailed()) {
            dispositions.add(CallDisposition.FAILED.name());
        }
        if (callLogSearchParameters.getNoAnswer()) {
            dispositions.add(CallDisposition.NO_ANSWER.name());
        }
        if (callLogSearchParameters.getUnknown()) {
            dispositions.add(CallDisposition.UNKNOWN.name());
        }
        return dispositions;
    }
}
