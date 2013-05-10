package org.motechproject.tasks.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isEmpty;

public final class KeyInformation {
    public static final String TRIGGER_PREFIX = "trigger";
    public static final String ADDITIONAL_DATA_PREFIX = "ad";

    private static final int DATA_PROVIDER_ID_IDX = 1;
    private static final int OBJECT_TYPE_IDX = 2;
    private static final int OBJECT_ID_IDX = 3;
    private static final int EVENTK_KEY_IDX = 4;

    private String originalKey;
    private String prefix;
    private String dataProviderId;
    private String objectType;
    private Long objectId;
    private String key;
    private List<String> manipulations;

    private KeyInformation(String originalKey, String prefix, String key, List<String> manipulations) {
        this.originalKey = originalKey;
        this.prefix = prefix;
        this.key = key;
        this.manipulations = manipulations;
    }

    private KeyInformation(String originalKey, String prefix, String dataProviderId, String objectType,
                           Long objectId, String key, List<String> manipulations) {
        this.originalKey = originalKey;
        this.prefix = prefix;
        this.dataProviderId = dataProviderId;
        this.objectType = objectType;
        this.objectId = objectId;
        this.key = key;
        this.manipulations = manipulations;
    }

    public static KeyInformation parse(String input) {
        List<String> manipulations = new ArrayList<>();
        KeyInformation key;

        int questionMarkIndex = input.indexOf('?');
        String withoutManipulation = questionMarkIndex == -1 ? input : input.substring(0, questionMarkIndex);
        String allManipulations = questionMarkIndex == -1 ? null : input.substring(questionMarkIndex + 1);

        if (allManipulations != null) {
            manipulations.addAll(Arrays.asList(allManipulations.split("\\?")));
        }

        int prefixIndex = withoutManipulation.indexOf('.');
        String prefix = prefixIndex != -1 ? withoutManipulation.substring(0, prefixIndex) : "";

        withoutManipulation = withoutManipulation.substring(prefixIndex + 1);

        if (prefix.equalsIgnoreCase(TRIGGER_PREFIX)) {
            key = new KeyInformation(input, prefix, withoutManipulation, manipulations);
        } else if (prefix.equalsIgnoreCase(ADDITIONAL_DATA_PREFIX)) {
            Pattern pattern = Pattern.compile("([a-zA-Z0-9]+)\\.([a-zA-Z0-9]+)#(\\d+)\\.(.+)");
            Matcher matcher = pattern.matcher(withoutManipulation);

            if (matcher.matches()) {
                String dataProviderId = matcher.group(DATA_PROVIDER_ID_IDX);
                String objectType = matcher.group(OBJECT_TYPE_IDX);
                Long objectId = Long.valueOf(matcher.group(OBJECT_ID_IDX));
                String eventKey = matcher.group(EVENTK_KEY_IDX);

                key = new KeyInformation(input, prefix, dataProviderId, objectType, objectId, eventKey, manipulations);
            } else {
                throw new IllegalArgumentException("Incorrect format for key from additional data");
            }
        } else {
            throw new IllegalArgumentException("Key must be from trigger or additional data");
        }

        return key;
    }

    public static List<KeyInformation> parseAll(String input) {
        List<KeyInformation> keys = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher matcher = pattern.matcher(isEmpty(input) ? "" : input);

        while (matcher.find()) {
            keys.add(KeyInformation.parse(matcher.group(1)));
        }

        return keys;
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

    public String getKey() {
        return key;
    }

    public List<String> getManipulations() {
        return manipulations;
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalKey, prefix, dataProviderId, objectType, objectId, key, manipulations);
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
                Objects.equals(this.key, other.key) &&
                Objects.equals(this.manipulations, other.manipulations);
    }
}
