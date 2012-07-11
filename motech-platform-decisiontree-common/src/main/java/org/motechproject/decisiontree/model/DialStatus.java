package org.motechproject.decisiontree.model;

public enum DialStatus {
    completed,
    failed,
    busy,
    noAnswer("no-answer");
    private String title;

    private DialStatus() {
        title = name();
    }

    DialStatus(String s) {
        title = s;
    }

    public static boolean isValid(String key) {
        for (DialStatus dialStatus : values()) {
            if (dialStatus.title.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return title == null ? name() : title;
    }
}
