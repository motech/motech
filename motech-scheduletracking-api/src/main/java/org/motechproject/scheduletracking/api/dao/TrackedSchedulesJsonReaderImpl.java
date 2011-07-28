package org.motechproject.scheduletracking.api.dao;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
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
        Type campaignListType = new TypeToken<List<ScheduleRecord>>() {
        }.getType();
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(definitionFile);
        try {
            String jsonText = IOUtils.toString(inputStream);
            System.out.println(jsonText);
            return new Gson().fromJson(jsonText, campaignListType);
        } catch (IOException e) {
            throw new JsonIOException(e);
        }

//        return (List<ScheduleRecord>) motechJsonReader.readFromFile(definitionFile, new TypeToken<List<ScheduleRecord>>() {
//        }.getType());
    }
}
