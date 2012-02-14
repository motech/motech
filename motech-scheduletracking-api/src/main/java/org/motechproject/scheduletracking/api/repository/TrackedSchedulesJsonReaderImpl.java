package org.motechproject.scheduletracking.api.repository;

import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.scheduletracking.api.domain.userspecified.ScheduleRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

@Component
public class TrackedSchedulesJsonReaderImpl implements TrackedSchedulesJsonReader {

    private List<String> definitionFilenames;
    private MotechJsonReader motechJsonReader;
    private String definitionsDirectoryName;

    @Autowired
    public TrackedSchedulesJsonReaderImpl(@Value("#{schedule_tracking['schedule.definitions.directory']}") String definitionsDirectoryName) {
        String schedulesDirectoryPath = getClass().getResource(definitionsDirectoryName).getPath();
        File[] definitionFiles = new File(schedulesDirectoryPath).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String filename) {
                return filename.endsWith(".json");
            }
        });
        definitionFilenames = extract(definitionFiles, on(File.class).getName());
        this.definitionsDirectoryName = definitionsDirectoryName;
        this.motechJsonReader = new MotechJsonReader();
    }

    @Override
    public List<ScheduleRecord> records() {
        List<ScheduleRecord> scheduleRecords = new ArrayList<ScheduleRecord>();
        Type type = new TypeToken<ScheduleRecord>() {
        }.getType();
        for (String filename : definitionFilenames)
            scheduleRecords.add((ScheduleRecord) motechJsonReader.readFromFile(definitionsDirectoryName + "/" + filename, type));
        return scheduleRecords;
    }
}
