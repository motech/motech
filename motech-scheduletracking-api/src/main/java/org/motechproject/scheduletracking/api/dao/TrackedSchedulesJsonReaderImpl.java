package org.motechproject.scheduletracking.api.dao;

import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;

import java.util.List;

public class TrackedSchedulesJsonReaderImpl implements TrackedSchedulesJsonReader {
    private String definitionFile;
    private MotechJsonReader motechJsonReader;

    public TrackedSchedulesJsonReaderImpl() {
        this(System.getProperty("trackedschedule.definition.file"));
    }

    public TrackedSchedulesJsonReaderImpl(String definitionFileName) {
        this(definitionFileName, new MotechJsonReader());
    }

    TrackedSchedulesJsonReaderImpl(String definitionFileName, MotechJsonReader motechJsonReader) {
        this.definitionFile = definitionFileName;
        this.motechJsonReader = motechJsonReader;
        if (definitionFileName == null) throw new NullPointerException();
    }

    @Override
    public List<ScheduleRecord> records() {
        return (List<ScheduleRecord>) motechJsonReader.readFromFile(definitionFile, new TypeToken<List<ScheduleRecord>>() {}.getType());
    }
}
