package org.motechproject.tasks.web.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.commons.api.MotechException;
import org.motechproject.config.SettingsFacade;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SettingsDto {

    public static final String TASK_POSSIBLE_ERRORS = "task.possible.errors";
    public static final String TASK_PROPERTIES_FILE_NAME = "settings.properties";

    private String taskPossibleErrors;
    private Map<String, String> taskRetries;

    public SettingsDto() {
        this(null, null);
    }

    public SettingsDto(String taskPossibleErrors, Map<String, String> taskRetries) {
        this.taskPossibleErrors = taskPossibleErrors;
        this.taskRetries = CollectionUtils.isEmpty(taskRetries) ? new HashMap<String, String>() : taskRetries;
    }


    public String getTaskPossibleErrors() {
        return taskPossibleErrors;
    }

    public void setTaskPossibleErrors(String taskPossibleErrors) {
        this.taskPossibleErrors = taskPossibleErrors;
    }

    public Map<String, String> getTaskRetries()  {
        if (this.taskRetries == null) {
            this.taskRetries = new HashMap<String, String>();
        }
        return this.taskRetries;
    }

    public void setTaskRetries(Map<String, String> taskRetries)  {
        if (this.taskRetries == null) {
            this.taskRetries = new HashMap<String, String>();
        } else {
            this.taskRetries = taskRetries;
        }
    }

    public SettingsDto getProperties(SettingsDto dto, SettingsFacade settingsFacade) {
        dto.setTaskPossibleErrors(settingsFacade.getProperty(TASK_POSSIBLE_ERRORS));

        try {
            InputStream retries = settingsFacade.getRawConfig(TASK_PROPERTIES_FILE_NAME);
            Properties props = new Properties();
            props.load(retries);
            //props
            if (props != null) {
                for (Map.Entry<Object, Object> entry : props.entrySet()) {
                    dto.taskRetries.put((String) entry.getKey(), (String) entry.getValue());
                }
            }
        } catch (IOException e) {
            throw new MotechException("Error loading raw file config to properties", e);
        } catch (NullPointerException e) {
            dto.setTaskRetries(new HashMap<String, String>());
        }

        return dto;
    }

    @JsonIgnore
    public Properties getTaskRetriesProps() {
        Properties props = new Properties();
        props.putAll(this.taskRetries);
        return props;
    }

    @JsonIgnore
    public boolean isValid() {
        if (StringUtils.isEmpty(taskPossibleErrors) || !StringUtils.isNumeric(taskPossibleErrors)) {
            return false;
        }
        return true;
    }

}
