package org.motechproject.mds.event;

import org.apache.commons.lang.StringUtils;
import org.motechproject.event.MotechEvent;
import org.motechproject.mds.domain.EntityInfo;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.mds.util.ClassName.simplifiedModuleName;
import static org.motechproject.mds.util.Constants.MDSEvents.BASE_SUBJECT;
import static org.motechproject.mds.util.Constants.MDSEvents.ENTITY_CLASS;
import static org.motechproject.mds.util.Constants.MDSEvents.ENTITY_NAME;
import static org.motechproject.mds.util.Constants.MDSEvents.MODULE_NAME;
import static org.motechproject.mds.util.Constants.MDSEvents.NAMESPACE;
import static org.motechproject.mds.util.Constants.MDSEvents.OBJECT_ID;

/**
 * The <code>MDSCrudEvents</code> class is responsible for creating MDS CRUD events.
 */
public final class CrudEventBuilder {

    private CrudEventBuilder() { }

    public static MotechEvent buildEvent(String module, String namespace, String entity, String entityClassName,
                                         CrudEventType action, Long id) {
        Map<String, Object> params = new HashMap<>();

        String simplifiedModuleName = simplifiedModuleName(module);

        params.put(ENTITY_NAME, entity);
        params.put(OBJECT_ID, id);
        params.put(ENTITY_CLASS, entityClassName);
        setIfNotBlank(params, MODULE_NAME, simplifiedModuleName);
        setIfNotBlank(params, NAMESPACE, namespace);

        String subject = createSubject(simplifiedModuleName, namespace, entity, action);

        return new MotechEvent(subject, params);
    }

    public static String createSubject(EntityInfo entity, String action) {
        return createSubject(entity.getModule(), entity.getNamespace(), entity.getEntityName(), action);
    }

    public static String createSubject(String module, String namespace, String entity, CrudEventType action) {
        return createSubject(module, namespace, entity, action.toString());
    }

    public static String createSubject(String module, String namespace, String entity, String action) {
        String subject;
        String simplifiedModuleName = simplifiedModuleName(module);

        if (StringUtils.isBlank(module)) {
            subject = BASE_SUBJECT + entity + "." + action;
        } else if (StringUtils.isBlank(namespace)) {
            subject = BASE_SUBJECT + simplifiedModuleName + "." + entity + "." + action;
        } else {
            subject = BASE_SUBJECT + simplifiedModuleName + "." + namespace + "." + entity + "." + action;
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
