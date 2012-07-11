package org.motechproject.server.verboice.domain;

public class Say implements VerboiceVerb {
    private int loop;
    private String voice;
    private String text;

    public Say(String text) {
        this(text, "man", 1);
    }

    public Say(String text, String voice, int loop) {
        this.voice = voice;
        this.loop = loop;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public int getLoop() {
        return loop;
    }

    @Override
    public String toXMLString() {
        return String.format("<Say voice=\"%s\" loop=\"%s\">%s</Say>", voice, loop, text);
    }
}
