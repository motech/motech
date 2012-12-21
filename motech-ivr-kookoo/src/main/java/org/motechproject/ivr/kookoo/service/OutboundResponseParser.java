package org.motechproject.ivr.kookoo.service;

import org.springframework.stereotype.Component;

@Component
public class OutboundResponseParser {

    public Boolean isError(String response) {
        String status = response.substring(response.indexOf("<status>"), response.indexOf("</status>"));
        return status.equals("<status>error");
    }

    public String getMessage(String response) {
        String message = response.substring(response.indexOf("<message>"), response.indexOf("</message>"));
        String[] messages = message.split("<message>");
        return messages.length > 0 ? messages[1] :  "";
    }
}
