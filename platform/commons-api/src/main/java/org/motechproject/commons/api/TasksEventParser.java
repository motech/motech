package org.motechproject.commons.api;


import java.util.Map;

/**
 * The <code>TasksEventParser</code> interface provides a way for modules to define
 * a custom way to handle trigger events. Before event parameters or subject are parsed,
 * the Tasks module will first check if received event contains parameter with key
 * {@literal custom_tasks_event_parser} and if so, it will look for custom parser that
 * matches the name exposed via <code>getName()</code> method. If any module wants
 * to use a custom event parser, they should simply implement this interface and
 * expose it as OSGi service.
 */
public interface TasksEventParser {

    String CUSTOM_PARSER_EVENT_KEY = "org.motechproject.tasks.custom_event_parser";

    /**
     * Given a map of event parameters, parses them in a user-defined way to receive
     * a custom map of event parameters
     *
     * @param eventSubject The original event subject
     * @param eventParameters Initial event parameters
     * @return Custom, parsed event parameters, that will be used instead of initial params
     */
    Map<String, Object> parseEventParameters(String eventSubject, Map<String, Object> eventParameters);

    /**
     * Adjusts event subject of the event. Thanks to this, we are able to map events of the same event subject
     * to different triggers. If there's no need to modify the subject of an event (eg. one event should map
     * only one trigger), simply return the original subject, that is passed as an argument.
     *
     * @param eventSubject The original event subject
     * @param eventParameters Initial event parameters
     * @return Custom event subject
     */
    String parseEventSubject(String eventSubject, Map<String, Object> eventParameters);

    /**
     * Returns the name of the module that registers custom parser. Tasks module will look
     * for all registered event parsers and try to match the name passed in the event parameter
     * <code>org.motechproject.tasks.custom_event_parser</code> with the name returned by this method. If there's
     * a match, a custom parser with matched name will be used.
     *
     * @return Module name that registers custom event parser
     */
    String getName();
}
