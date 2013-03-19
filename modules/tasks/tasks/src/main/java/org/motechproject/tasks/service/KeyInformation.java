package org.motechproject.tasks.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.motechproject.tasks.service.HandlerUtil.ADDITIONAL_DATA_PREFIX;
import static org.motechproject.tasks.service.HandlerUtil.TRIGGER_PREFIX;

class KeyInformation {
    private static final int DATA_PROVIDER_ID_IDX = 1;
    private static final int OBJECT_TYPE_IDX = 2;
    private static final int OBJECT_ID_IDX = 3;
    private static final int EVENTK_KEY_IDX = 4;

    private String originalKey;
    private String prefix;
    private String dataProviderId;
    private String objectType;
    private Long objectId;
    private String eventKey;
    private List<String> manipulations;

    public KeyInformation(String key) {
        this.originalKey = key;

        int questionMarkIndex = key.indexOf('?');
        String withoutManipulation = questionMarkIndex == -1 ? key : key.substring(0, questionMarkIndex);
        String allManipulations = questionMarkIndex == -1 ? null : key.substring(questionMarkIndex + 1);

        if (allManipulations != null) {
            this.manipulations = Arrays.asList(allManipulations.split("\\?"));
        }

        int prefixIndex = withoutManipulation.indexOf('.');
        prefix = prefixIndex != -1 ? withoutManipulation.substring(0, prefixIndex) : "";
        withoutManipulation = withoutManipulation.substring(prefixIndex + 1);

        if (prefix.equalsIgnoreCase(TRIGGER_PREFIX)) {
            eventKey = withoutManipulation;
        } else if (prefix.equalsIgnoreCase(ADDITIONAL_DATA_PREFIX)) {
            Pattern pattern = Pattern.compile("([a-zA-Z0-9]+)\\.([a-zA-Z0-9]+)#(\\d+)\\.(.+)");
            Matcher matcher = pattern.matcher(withoutManipulation);

            if (matcher.matches()) {
                dataProviderId = matcher.group(DATA_PROVIDER_ID_IDX);
                objectType = matcher.group(OBJECT_TYPE_IDX);
                objectId = Long.valueOf(matcher.group(OBJECT_ID_IDX));
                eventKey = matcher.group(EVENTK_KEY_IDX);
            } else {
                throw new IllegalArgumentException("Incorrect format for key from additional data");
            }
        } else {
            throw new IllegalArgumentException("Key must be from trigger or additional data");
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

    @Override
    public int hashCode() {
        return Objects.hash(originalKey, prefix, dataProviderId, objectType, objectId, eventKey, manipulations);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final KeyInformation other = (KeyInformation) obj;

        return Objects.equals(this.originalKey, other.originalKey) &&
                Objects.equals(this.prefix, other.prefix) &&
                Objects.equals(this.dataProviderId, other.dataProviderId) &&
                Objects.equals(this.objectType, other.objectType) &&
                Objects.equals(this.objectId, other.objectId) &&
                Objects.equals(this.eventKey, other.eventKey) &&
                Objects.equals(this.manipulations, other.manipulations);
    }
}
