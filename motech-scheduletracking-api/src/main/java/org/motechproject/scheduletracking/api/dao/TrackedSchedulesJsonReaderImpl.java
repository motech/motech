package org.motechproject.scheduletracking.api.dao;

import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;

@Component
public class TrackedSchedulesJsonReaderImpl implements TrackedSchedulesJsonReader {
    private String definitionFile;
    private MotechJsonReader motechJsonReader;

    @Autowired
    public TrackedSchedulesJsonReaderImpl(@Value("#{scheduletracking['trackedschedule.definition.file']}") String definitionFileName,
                                          MotechJsonReader motechJsonReader) {
        this.definitionFile = definitionFileName;
        this.motechJsonReader = motechJsonReader;
        if (definitionFileName == null) throw new NullPointerException();
    }

    @Override
    public List<ScheduleRecord> records() {
        Type type = new TypeToken<List<ScheduleRecord>>() {
        }.getType();
        return (List<ScheduleRecord>) motechJsonReader.readFromFile(definitionFile, type);
    }
}
