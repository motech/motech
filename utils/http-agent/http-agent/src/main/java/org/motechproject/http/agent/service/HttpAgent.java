package org.motechproject.http.agent.service;

import org.motechproject.http.agent.domain.Method;

public interface HttpAgent {

    void execute(String url, Object data, Method method);

    void executeSync(String url, Object data, Method method);
}
