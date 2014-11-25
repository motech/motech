package org.motechproject.mds.event;

import org.apache.commons.lang.StringUtils;
import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.mds.util.ClassName.simplifiedModuleName;
import static org.motechproject.mds.util.Constants.MDSEvents.BASE_SUBJECT;
import static org.motechproject.mds.util.Constants.MDSEvents.ENTITY_NAME;
import static org.motechproject.mds.util.Constants.MDSEvents.MODULE_NAME;
import static org.motechproject.mds.util.Constants.MDSEvents.NAMESPACE;
import static org.motechproject.mds.util.Constants.MDSEvents.OBJECT_ID;

/**
 * The <code>MDSCrudEvents</code> class is responsible for creating MDS CRUD events.
 */
public final class CrudEventBuilder {

    private CrudEventBuilder() { }

    public static MotechEvent buildEvent(String module, String entity, String namespace, CrudEventType action, Long id) {
        Map<String, Object> params = new HashMap<>();

        String simplifiedModuleName = simplifiedModuleName(module);

        params.put(ENTITY_NAME, entity);
        params.put(OBJECT_ID, id);

        setIfNotBlank(params, MODULE_NAME, simplifiedModuleName);
        setIfNotBlank(params, NAMESPACE, namespace);

        String subject = createSubject(simplifiedModuleName, entity, namespace, action);

        return new MotechEvent(subject, params);
    }

    public static String createSubject(String module, String entity, String namespace, CrudEventType action) {
        String subject;
        String simplifiedModuleName = simplifiedModuleName(module);

        if (StringUtils.isBlank(module)) {
            subject = BASE_SUBJECT + entity + "." + action.toString();
        } else if (StringUtils.isBlank(namespace)) {
            subject = BASE_SUBJECT + simplifiedModuleName + "." + entity + "." + action.toString();
        } else {
            subject = BASE_SUBJECT + simplifiedModuleName + "." + namespace + "." + entity + "." + action.toString();
        }

        return subject;
    }

    private static void setIfNotBlank(Map<String, Object> params, String property, String value) {
        if (StringUtils.isBlank(property) || StringUtils.isBlank(value)) {
            return;
        }
        params.put(property, value);
    }
}
