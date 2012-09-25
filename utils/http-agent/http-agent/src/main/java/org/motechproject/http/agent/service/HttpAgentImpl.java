package org.motechproject.http.agent.service;

import org.motechproject.event.MotechEvent;
import org.motechproject.http.agent.components.AsynchronousCall;
import org.motechproject.http.agent.components.SynchronousCall;
import org.motechproject.http.agent.domain.EventDataKeys;
import org.motechproject.http.agent.domain.EventSubjects;
import org.motechproject.http.agent.domain.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class HttpAgentImpl implements HttpAgent {

    private AsynchronousCall asynchronousCall;
    private SynchronousCall synchronousCall;

    @Autowired
    public HttpAgentImpl(AsynchronousCall asynchronousCall, SynchronousCall synchronousCall) {
        this.asynchronousCall = asynchronousCall;
        this.synchronousCall = synchronousCall;
    }

    @Override
    public void execute(String url, Object data, Method method) {
        HashMap<String, Object> parameters = constructParametersFrom(url, data, method);
        sendMessage(parameters);
    }

    @Override
    public void executeSync(String url, Object data, Method method) {
        HashMap<String, Object> parameters = constructParametersFrom(url, data, method);
        sendMessageSync(parameters);
    }


    private HashMap<String, Object> constructParametersFrom(String url, Object data, Method method) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(EventDataKeys.URL, url);
        parameters.put(EventDataKeys.METHOD, method);
        parameters.put(EventDataKeys.DATA, data);
        return parameters;
    }

    private void sendMessage(HashMap<String, Object> parameters) {
        MotechEvent motechEvent = new MotechEvent(EventSubjects.HTTP_REQUEST, parameters);
        asynchronousCall.send(motechEvent);
    }

    private void sendMessageSync(HashMap<String, Object> parameters) {
        MotechEvent motechEvent = new MotechEvent(EventSubjects.HTTP_REQUEST, parameters);
        synchronousCall.send(motechEvent);
    }
}
