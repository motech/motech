package org.motechproject.ivr.calllog.service;

import org.motechproject.ivr.calllog.domain.CallRecord;
import org.motechproject.ivr.calllog.domain.CallLogSearchParameters;
import org.motechproject.ivr.calllog.repository.AllCallRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CallRecordsSearchService {
    private AllCallRecords allCallDetailRecords;
    private static final int PAGE_SIZE = 20;

    @Autowired
    public CallRecordsSearchService(AllCallRecords allCallDetailRecords) {
        this.allCallDetailRecords = allCallDetailRecords;
    }

    public List<CallRecord> search(CallLogSearchParameters callLogSearchParameters) {

        return allCallDetailRecords.search(callLogSearchParameters.getPhoneNumber(),
                callLogSearchParameters.getFromDateAsDateTime(),
                callLogSearchParameters.getToDateAsDateTime(),
                callLogSearchParameters.getMinDuration(),
                callLogSearchParameters.getMaxDuration(),
                mapToDispositions(callLogSearchParameters), callLogSearchParameters.getPage(), PAGE_SIZE, callLogSearchParameters.getSortColumn(), callLogSearchParameters.isSortReverse());
    }

    public long count(CallLogSearchParameters callLogSearchParameters) {
        double numOfPages = allCallDetailRecords.countRecords(callLogSearchParameters.getPhoneNumber(),
                callLogSearchParameters.getFromDateAsDateTime(),
                callLogSearchParameters.getToDateAsDateTime(),
                callLogSearchParameters.getMinDuration(),
                callLogSearchParameters.getMaxDuration(), mapToDispositions(callLogSearchParameters)) / (PAGE_SIZE * 1.0);
        return Math.round(Math.ceil(numOfPages));
    }

    public List<String> getAllPhoneNumbers() {
        return allCallDetailRecords.getAllPhoneNumbers();
    }

    public long findMaxCallDuration() {
        return allCallDetailRecords.findMaxCallDuration();
    }

    private List<String> mapToDispositions(CallLogSearchParameters callLogSearchParameters) {
        List<String> dispositions = new ArrayList<>();

        if (callLogSearchParameters.getAnswered()) {
            dispositions.add(CallRecord.Disposition.ANSWERED.name());
        }
        if (callLogSearchParameters.getBusy()) {
            dispositions.add(CallRecord.Disposition.BUSY.name());
        }
        if (callLogSearchParameters.getFailed()) {
            dispositions.add(CallRecord.Disposition.FAILED.name());
        }
        if (callLogSearchParameters.getNoAnswer()) {
            dispositions.add(CallRecord.Disposition.NO_ANSWER.name());
        }
        if (callLogSearchParameters.getUnknown()) {
            dispositions.add(CallRecord.Disposition.UNKNOWN.name());
        }
        return dispositions;
    }
}
