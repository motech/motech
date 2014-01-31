package org.motechproject.tasks.domain;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.annotate.JsonValue;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Defines the type of various manipulations used in a task for both triggers and data sources.
 */

public enum ManipulationType {
    TOLOWER("toLower", ManipulationTarget.STRING),
    TOUPPER("toUpper", ManipulationTarget.STRING),
    CAPITALIZE("capitalize", ManipulationTarget.STRING),
    JOIN("join", ManipulationTarget.STRING),
    FORMAT("format", ManipulationTarget.STRING),
    SUBSTRING("substring", ManipulationTarget.STRING),
    SPLIT("split", ManipulationTarget.STRING),
    DATETIME("dateTime", ManipulationTarget.DATE, ManipulationTarget.DATE),
    PLUSDAYS("plusDays", ManipulationTarget.DATE),
    UNKNOWN("unknown", ManipulationTarget.ALL);

    private final String value;
    private final ManipulationTarget target;
    private ManipulationTarget[] forbiddenResultTypes;

    public Object parse(String value) {
        return value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonValue
    public ManipulationTarget getTarget() {
        return target;
    }

    private ManipulationType(String value, ManipulationTarget target, ManipulationTarget ... forbiddenResultTypes) {
        this.value = value;
        this.target = target;
        this.forbiddenResultTypes = forbiddenResultTypes;
    }

    private ManipulationType(String value, ManipulationTarget target) {
        this.value = value;
        this.target = target;
        this.forbiddenResultTypes = new ManipulationTarget[0];
    }

    public static ManipulationType fromString(String string) {
        ManipulationType result = null;

        if (isNotBlank(string)) {
            for (ManipulationType level : ManipulationType.values()) {
                if (level.getValue().equalsIgnoreCase(string)) {
                    result = level;
                    break;
                }
            }
        }

        if (result == null) {
            result = ManipulationType.UNKNOWN;
        }

        return result;
    }

    public boolean allowResultType(ManipulationTarget resultType) {
        if (ArrayUtils.contains(forbiddenResultTypes, ManipulationTarget.ALL)) {
            return false;
        }
        if (ArrayUtils.contains(forbiddenResultTypes, resultType)) {
            return false;
        }
        return true;
    }
}
