package org.motechproject.server.verboice.domain;

@Deprecated
public class Play implements VerboiceVerb {
    private int loop;
    private String fileUrl;

    public Play(String url) {
        this(url, 1);
    }
    
    public Play(String fileUrl, int loop) {
        this.loop = loop;
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public int getLoop() {
        return loop;
    }

    @Override
    public String toXMLString() {
        return String.format("<Play loop=\"%s\">%s</Play>", loop, fileUrl);
    }
}
