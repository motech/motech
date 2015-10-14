package org.motechproject.mds.web.util.mock;

public class SafetyPolicy {

    private Manufacturer manufacturer;
    private String text;

    public SafetyPolicy(Manufacturer manufacturer, String text) {
        this.manufacturer = manufacturer;
        this.text = text;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
