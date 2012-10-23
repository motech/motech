package org.motechproject.scheduletracking.api.repository;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.CharEncoding;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

public class TrackedSchedulesJsonReaderImpl implements TrackedSchedulesJsonReader {
    private MotechJsonReader motechJsonReader;
    private String jsonExtension = ".json";

    public TrackedSchedulesJsonReaderImpl() {
        this.motechJsonReader = new MotechJsonReader();
    }

    @Override
    public List<ScheduleRecord> getAllSchedules(String definitionsDirectoryName) {
        List<ScheduleRecord> scheduleRecords = new ArrayList<>();
        Type type = new TypeToken<ScheduleRecord>() { } .getType();

        for (String filename : getAllFileNames(definitionsDirectoryName)) {
            scheduleRecords.add((ScheduleRecord) motechJsonReader.readFromFile(definitionsDirectoryName + "/" + filename, type));
        }
        return scheduleRecords;
    }

    @Override
    public ScheduleRecord getSchedule(String schduleJson) {
        Type type = new TypeToken<ScheduleRecord>() { } .getType();
        return (ScheduleRecord) motechJsonReader.readFromString(schduleJson, type);
    }

    private List<String> getAllFileNames(String definitionsDirectoryName) {
        String resourcePath = getClass().getResource(definitionsDirectoryName).getPath();
        String schedulesDirectoryPath;

        try {
            schedulesDirectoryPath = URLDecoder.decode(resourcePath, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            schedulesDirectoryPath = resourcePath;
        }

        File[] files = new File(schedulesDirectoryPath).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String filename) {
                return filename.endsWith(jsonExtension);
            }
        });

        return extract(files, on(File.class).getName());
    }
}
