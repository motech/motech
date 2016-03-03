package org.motechproject.mds.testutil.records.display;

public enum DisplayTestEnum {

    MONDAY("Monday"), TUESDAY("Tuesday"), WEDNESDAY("Wednesday");

    private final String disp;

    DisplayTestEnum(String disp) {
        this.disp = disp;
    }

    public String getDisp() {
        return disp;
    }

    public static String valuesMap() {
        return "MONDAY:Monday\nTUESDAY:Tuesday\nWEDNESDAY:Wednesday";
    }
}
