package org.motechproject.http.agent.service;

public interface HttpAgent {

    void execute(String url, Object data, Method method);

    void executeSync(String url, Object data, Method method);
}
