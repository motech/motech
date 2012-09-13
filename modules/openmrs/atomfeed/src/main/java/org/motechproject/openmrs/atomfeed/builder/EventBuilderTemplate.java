package org.motechproject.openmrs.atomfeed.builder;

import org.motechproject.event.MotechEvent;
import org.motechproject.openmrs.atomfeed.events.EventDataKeys;
import org.motechproject.openmrs.atomfeed.model.Entry;

import java.util.HashMap;
import java.util.Map;

public abstract class EventBuilderTemplate {

    private final Entry entry;

    public EventBuilderTemplate(Entry entry) {
        this.entry = entry;
    }

    public MotechEvent toEvent() {
        String subject = getSubjectFromAction(entry.getAction());
        return buildMotechEvent(entry, subject);
    }

    private String getSubjectFromAction(String action) {
        if ("create".equals(action)) {
            return getCreateAction();
        } else if ("update".equals(action)) {
            return getUpdateAction();
        } else if ("void".equals(action)) {
            return getVoidedAction();
        } else if ("delete".equals(action)) {
            return getDeleteAction();
        }

        return null;
    }

    protected abstract String getCreateAction();

    protected abstract String getUpdateAction();

    protected abstract String getVoidedAction();

    protected abstract String getDeleteAction();

    private MotechEvent buildMotechEvent(Entry entry, String subject) {
        MotechEvent event = new MotechEvent(subject);
        event.getParameters().putAll(addDefaultParams(entry));
        return event;
    }

    private Map<String, Object> addDefaultParams(Entry entry) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EventDataKeys.UUID, extractEntryUuid(entry.getId()));
        params.put(EventDataKeys.AUTHOR, entry.getAuthor().getName());
        params.put(EventDataKeys.ACTION, entry.getAction());
        params.put(EventDataKeys.LINK, entry.getLink().getHref());
        params.put(EventDataKeys.UPDATED, entry.getUpdated());

        return params;
    }

    private Object extractEntryUuid(String id) {
        int index = id.indexOf("uuid:");
        if (index >= 0) {
            return id.substring(index + 5);
        }

        return "";
    }

}
