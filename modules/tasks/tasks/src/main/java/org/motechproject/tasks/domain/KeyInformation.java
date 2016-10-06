package org.motechproject.tasks.domain;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.tasks.service.impl.TaskTriggerHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * Object representation of dragged field from trigger or data source.
 * <p/>
 * This class represents a single dragged field from trigger or data source. This class does not
 * expose a public constructor. You have to use {@link #parse(String)} method if you want to parse
 * single field or {@link #parseAll(String)} if you want ot get all fields from a given string.
 *
 * @since 0.18
 */
public final class KeyInformation {
    /**
     * Prefix which is used for trigger fields.
     */
    public static final String TRIGGER_PREFIX = "trigger";

    /**
     * Prefix which is used for data source fields.
     */
    public static final String ADDITIONAL_DATA_PREFIX = "ad";

    /**
     * Prefix which is used for post action parameters fields.
     */
    public static final String POST_ACTION_PARAMETER_PREFIX = "pa";

    private static final int DATA_PROVIDER_NAME_IDX = 1;
    private static final int OBJECT_TYPE_IDX = 2;
    private static final int OBJECT_ID_IDX = 3;
    private static final int EVENT_KEY_IDX = 4;

    private static final int POST_ACTION_PARAM_ID_IDX = 1;
    private static final int POST_ACTION_PARAM_KEY_IDX = 2;

    private String originalKey;
    private String prefix;
    private String dataProviderName;
    private String objectType;
    private Long objectId;
    private String key;
    private List<String> manipulations;

    private KeyInformation(String originalKey, String prefix, String key, List<String> manipulations) {
        this(originalKey, prefix, null, null, null, key, manipulations);
    }

    private KeyInformation(String originalKey, String prefix, Long objectId, String key, List<String> manipulations) {
        this(originalKey, prefix, null, null, objectId, key, manipulations);
    }

    private KeyInformation(String originalKey, String prefix, String dataProviderName, String objectType,
                           Long objectId, String key, List<String> manipulations) {
        this.originalKey = originalKey;
        this.prefix = prefix;
        this.dataProviderName = dataProviderName;
        this.objectType = objectType;
        this.objectId = objectId;
        this.key = key;
        this.manipulations = manipulations;
    }

    /**
     * Parse given string to instance of {@link KeyInformation}.
     * <p/>
     * This method should be used to convert string representation of dragged field to instance of
     * {@link KeyInformation}.
     * <p/>
     * Argument has adhere to one of the following format:
     * <ul>
     * <li>trigger field format: <b>trigger.<i>eventKey</i></b></li>
     * <li>data source format: <b>ad.<i>dataProviderId</i>.<i>objectType</i>#<i>objectId</i>.<i>fieldKey</i></b></li>
     * <li>post action parameter format: <b>pa.<i>objectId</i>.<i>fieldKey</i></b></li>
     * </ul>
     * <p/>
     * Argument can also contain list of manipulation which should be executed on field before it
     * will be used by {@link TaskTriggerHandler} class.
     * Manipulations should be connected together by the <b>?</b> character.
     * <p/>
     * Example of input argument:
     * <ul>
     * <li>ad.279f5fdf60700d9717270b1ae3011eb1.CaseInfo#0.fieldValues.phu_id</li>
     * <li>trigger.message?format(Ala,cat)?capitalize</li>
     * </ul>
     *
     * @param input string representation of a dragged field from trigger or data source
     * @return Object representation of a dragged field
     * @throws IllegalArgumentException exception is thrown if format for data source field is
     *                                  incorrect or if the dragged field is not from trigger or
     *                                  data source.
     */
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
            Pattern pattern = Pattern.compile("([a-zA-Z0-9\\-_]+)\\.([\\.a-zA-Z0-9\\-_]+)#([a-zA-Z0-9]+)\\.(.+)");
            Matcher matcher = pattern.matcher(withoutManipulation);

            if (matcher.matches()) {
                String dataProviderName = matcher.group(DATA_PROVIDER_NAME_IDX);
                String objectType = matcher.group(OBJECT_TYPE_IDX);
                Long objectId = Long.valueOf(matcher.group(OBJECT_ID_IDX));
                String eventKey = matcher.group(EVENT_KEY_IDX);

                key = new KeyInformation(input, prefix, dataProviderName, objectType, objectId, eventKey, manipulations);
            } else {
                throw new IllegalArgumentException("Incorrect format for key from additional data");
            }
        } else if (prefix.equalsIgnoreCase(POST_ACTION_PARAMETER_PREFIX)) {
            Pattern pattern = Pattern.compile("([a-zA-Z0-9\\-_]+)\\.(.+)");
            Matcher matcher = pattern.matcher(withoutManipulation);

            if (matcher.matches()) {
                Long objectId = Long.valueOf(matcher.group(POST_ACTION_PARAM_ID_IDX));
                String fieldKey = matcher.group(POST_ACTION_PARAM_KEY_IDX);

                key = new KeyInformation(input, prefix, objectId, fieldKey, manipulations);
            } else {
                throw new IllegalArgumentException("Incorrect format for key from post action parameter.");
            }
        } else {
            throw new IllegalArgumentException("Key must be from trigger, additional data or post action parameter.");
        }

        return key;
    }

    /**
     * Find all fields from given input and convert them to the instance of {@link KeyInformation}.
     * <p/>
     * This method should be used to find and convert all of string representation of the field
     * from trigger and/or data sources. Fields in input have to adhere to one of the following formats:
     * <ul>
     * <li>trigger field format: <b>{{trigger.<i>eventKey</i>}}</b></li>
     * <li>data source format: <b>{{ad.<i>dataProviderId</i>.<i>objectType</i>#<i>objectId</i>.<i>fieldKey</i>}}</b></li>
     * <li>post action parameter format: <b>pa.<i>fieldKey</i></b></li>
     * </ul>
     * <p/>
     * To find fields in the input argument this method uses regular expression. When field is found
     * it is converted to an instance of {@link KeyInformation} by using the {@link #parse(String)}
     * method.
     * <p/>
     * Fields are found by the following regular expression: <b>\{\{((.*?))(\}\})(?![^(]*\))</b>.
     * The expression searches for strings that start with <i>{{</i> and end with <i>}}</i> and are not within <i>(</i> and <i>)</i>.
     * Because of manipulations which contain additional data in <i>(...)</i> needed to
     * execute manipulation on the field (e.g.: join needs to have the join character) and the text
     * in <i>(...)</i> can be another string representation of the dragged field, the expression
     * has to check if the field has this kind of manipulation.
     * <p/>
     * Example of input argument:
     * <ul>
     * <li>{{trigger.message?format(Ala,cat)?capitalize}}</li>
     * <li>You get the following message: {{trigger.message}}</li>
     * </ul>
     *
     * @param input string with one or more string representation of dragged fields from trigger
     *              and/or data sources
     * @return list of object representation of dragged fields.
     * @throws IllegalArgumentException in the same situations as the {@link #parse(String)}
     *                                  method.
     */
    public static List<KeyInformation> parseAll(String input) {
        List<KeyInformation> keys = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{\\{((.*?))(\\}\\})(?![^(]*\\))");
        Matcher matcher = pattern.matcher(isEmpty(input) ? "" : input);

        while (matcher.find()) {
            keys.add(KeyInformation.parse(matcher.group(1)));
        }

        return keys;
    }

    /**
     * Check if the field is from the trigger.
     *
     * @return true if the field is from the trigger otherwise false
     */
    public boolean fromTrigger() {
        return prefix.equalsIgnoreCase(TRIGGER_PREFIX);
    }

    /**
     * Check if the field is from the data source.
     *
     * @return true if the field is from the data source otherwise false
     */
    public boolean fromAdditionalData() {
        return prefix.equalsIgnoreCase(ADDITIONAL_DATA_PREFIX);
    }

    /**
     * Check if the field is from the post action parameter.
     *
     * @return true if the field is from the post action parameter otherwise false
     */
    public boolean fromPostActionParameter() {
        return prefix.equalsIgnoreCase(POST_ACTION_PARAMETER_PREFIX);
    }

    public String getPrefix() {
        return prefix;
    }

    /**
     * Get original representation of the dragged field.
     *
     * @return string representation of the field
     */
    public String getOriginalKey() {
        return originalKey;
    }

    public String getDataProviderName() {
        return dataProviderName;
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

    /**
     * Check if the field has any manipulations.
     *
     * @return true if the field has manipulations otherwise false
     */
    public boolean hasManipulations() {
        return !CollectionUtils.isEmpty(manipulations);
    }

    /**
     * Get manipulations assigned to the field.
     *
     * @return list of manipulations
     */
    public List<String> getManipulations() {
        return manipulations;
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalKey, prefix, dataProviderName, objectType, objectId, key, manipulations);
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
            Objects.equals(this.dataProviderName, other.dataProviderName) &&
            Objects.equals(this.objectType, other.objectType) &&
            Objects.equals(this.objectId, other.objectId) &&
            Objects.equals(this.key, other.key) &&
            Objects.equals(this.manipulations, other.manipulations);
    }
}
