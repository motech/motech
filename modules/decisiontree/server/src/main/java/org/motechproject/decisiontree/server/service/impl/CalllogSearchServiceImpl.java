package org.motechproject.decisiontree.server.service.impl;

import org.motechproject.decisiontree.core.CallDetail;
import org.motechproject.decisiontree.server.domain.CallDetailRecord;
import org.motechproject.decisiontree.server.repository.AllCallDetailRecords;
import org.motechproject.decisiontree.server.service.CalllogSearchParameters;
import org.motechproject.decisiontree.server.service.CalllogSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("calllogSearchService")
public class CalllogSearchServiceImpl implements CalllogSearchService {
    private AllCallDetailRecords allCallDetailRecords;
    private static final int PAGE_SIZE = 2;

    @Autowired
    public CalllogSearchServiceImpl(AllCallDetailRecords allCallDetailRecords) {
        this.allCallDetailRecords = allCallDetailRecords;
    }


    @Override
    public List<CallDetail> search(CalllogSearchParameters params) {

        return allCallDetailRecords.search(params.getPhoneNumber(),
                params.getFromDateAsDateTime(),
                params.getToDateAsDateTime(),
                params.getMinDuration(),
                params.getMaxDuration(),
                mapToDispositions(params), params.getPage(), PAGE_SIZE, params.getSortColumn(), params.isSortReverse());
    }

    @Override
    public long count(CalllogSearchParameters params) {
        double numOfPages = allCallDetailRecords.countRecords(params.getPhoneNumber(),
                params.getFromDateAsDateTime(),
                params.getToDateAsDateTime(),
                params.getMinDuration(),
                params.getMaxDuration(), mapToDispositions(params)) / (PAGE_SIZE * 1.0 );
        return Math.round(Math.ceil(numOfPages));
    }

    @Override
    public long findMaxCallDuration() {
        return allCallDetailRecords.findMaxCallDuration();
    }

    private List<String> mapToDispositions(CalllogSearchParameters params) {
        List<String> dispositions = new ArrayList<>();

        if (params.getAnswered()) {
            dispositions.add(CallDetailRecord.Disposition.ANSWERED.name());
        }
        if (params.getBusy()) {
            dispositions.add(CallDetailRecord.Disposition.BUSY.name());
        }
        if (params.getFailed()) {
            dispositions.add(CallDetailRecord.Disposition.FAILED.name());
        }
        if (params.getNoAnswer()) {
            dispositions.add(CallDetailRecord.Disposition.NO_ANSWER.name());
        }
        if (params.getUnknown()) {
            dispositions.add(CallDetailRecord.Disposition.UNKNOWN.name());
        }
        return dispositions;
    }

    @Override
    public List<String> getAllPhoneNumbers() {
        return allCallDetailRecords.getAllPhoneNumbers();
    }
}
