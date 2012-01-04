package org.motechproject.sms.api;

import java.util.ArrayList;
import java.util.List;

public class MessageSplitter {

    String headerTemplate;
    String footer;

    public MessageSplitter(String headerTemplate, String footer) {
        this.headerTemplate = headerTemplate;
        this.footer = footer;
    }

    public List<String> split(String message, int unitCapacity) {
        List<String> parts = new ArrayList<String>();
        if (message.length() <= unitCapacity) {
            parts.add(message);
            return parts;
        }
        int unitTextLength = unitCapacity - (getHeaderLength() + footer.length());
        int numberOfParts = getNumberOfParts(message, unitTextLength);
        for (int i = 0; i < numberOfParts; i++) {
            StringBuffer sb = new StringBuffer();
            sb.append(String.format(headerTemplate, i + 1, numberOfParts));
            sb.append(getPart(message, i, unitTextLength));
            if (i < numberOfParts - 1)
                sb.append("..");
            parts.add(sb.toString());
        }
        return parts;
    }

    private int getNumberOfParts(String message, int unitTextLength) {
        return (int) Math.ceil(message.length() / (double) unitTextLength);
    }

    private String getPart(String message, int index, int unitLength) {
        int start = index * unitLength;
        int end = start + unitLength < message.length()? start + unitLength : message.length();
        return message.substring(start, end);
    }

    private int getHeaderLength() {
        return String.format(headerTemplate, 0, 0).length();
    }
}
