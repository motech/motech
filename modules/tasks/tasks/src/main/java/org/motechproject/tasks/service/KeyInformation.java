package org.motechproject.tasks.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.motechproject.tasks.service.TaskTriggerHandler.ADDITIONAL_DATA_PREFIX;
import static org.motechproject.tasks.service.TaskTriggerHandler.TRIGGER_PREFIX;

class KeyInformation {
    private String originalKey;
    private String prefix;
    private String dataProviderId;
    private String objectType;
    private Long objectId;
    private String eventKey;
    private List<String> manipulations = new ArrayList<>();

    public KeyInformation(String key) {
        this.originalKey = key;

        int questionMarkIndex = key.indexOf('?');
        String withoutManipulation = questionMarkIndex == -1 ? key : key.substring(0, questionMarkIndex);
        String allManipulations = questionMarkIndex == -1 ? null : key.substring(questionMarkIndex);

        if (allManipulations != null) {
            this.manipulations.addAll(Arrays.asList(key.split("\\?")));
        }

        int prefixIndex = withoutManipulation.indexOf('.');
        prefix = withoutManipulation.substring(0, prefixIndex);
        withoutManipulation = withoutManipulation.substring(prefixIndex + 1);

        if (prefix.equalsIgnoreCase(TRIGGER_PREFIX)) {
            eventKey = withoutManipulation;
        } else if (prefix.equalsIgnoreCase(ADDITIONAL_DATA_PREFIX)) {
            Pattern pattern = Pattern.compile("([a-zA-Z0-9]+)\\.([a-zA-Z0-9]+)#(\\d+)\\.(.+)");
            Matcher matcher = pattern.matcher(withoutManipulation);

            if (matcher.matches()) {
                dataProviderId = matcher.group(1);
                objectType = matcher.group(2);
                objectId = Long.valueOf(matcher.group(3));
                eventKey = matcher.group(4);
            }
        }
    }

    public boolean fromTrigger() {
        return prefix.equalsIgnoreCase(TRIGGER_PREFIX);
    }

    public boolean fromAdditionalData() {
        return prefix.equalsIgnoreCase(ADDITIONAL_DATA_PREFIX);
    }

    public String getOriginalKey() {
        return originalKey;
    }

    public String getDataProviderId() {
        return dataProviderId;
    }

    public String getObjectType() {
        return objectType;
    }

    public Long getObjectId() {
        return objectId;
    }

    public String getEventKey() {
        return eventKey;
    }

    public List<String> getManipulations() {
        return manipulations;
    }
}