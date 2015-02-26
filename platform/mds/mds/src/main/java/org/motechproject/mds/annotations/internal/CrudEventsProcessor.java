package org.motechproject.mds.annotations.internal;

import org.apache.commons.lang.ArrayUtils;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.dto.TrackingDto;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The <code>CrudEventsProcessor</code> provides a mechanism for processing
 * {@link org.motechproject.mds.annotations.CrudEvents} annotation of a single
 * class with {@link org.motechproject.mds.annotations.Entity} annotation.
 *
 * @see org.motechproject.mds.annotations.CrudEvents
 * @see org.motechproject.mds.annotations.Entity
 */
@Component
public class CrudEventsProcessor implements Processor<CrudEvents> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrudEventsProcessor.class);

    private Class clazz;
    private TrackingDto trackingDto;

    @Override
    public Class<CrudEvents> getAnnotationType() {
        return CrudEvents.class;
    }

    @Override
    public void execute(Bundle bundle) {
        CrudEvents annotation = ReflectionsUtil.getAnnotationClassLoaderSafe(clazz, clazz, CrudEvents.class);

        if (null != annotation) {
            CrudEventType[] crudEventTypes = annotation.value();
            if (ArrayUtils.isEmpty(crudEventTypes)) {
                LOGGER.error("CrudEvents annotation for {} is specified but its value is missing.", clazz.getName());
            } else {
                forEach:
                for (CrudEventType crudEventType : crudEventTypes) {
                    switch (crudEventType) {
                        case CREATE:
                            trackingDto.setAllowCreateEvent(true);
                            break;
                        case UPDATE:
                            trackingDto.setAllowUpdateEvent(true);
                            break;
                        case DELETE:
                            trackingDto.setAllowDeleteEvent(true);
                            break;
                        case ALL:
                            trackingDto.setAllowCreateEvent(true);
                            trackingDto.setAllowUpdateEvent(true);
                            trackingDto.setAllowDeleteEvent(true);
                            break forEach;
                    }
                }
            }
        } else {
            trackingDto.setAllowCreateEvent(false);
            trackingDto.setAllowDeleteEvent(false);
            trackingDto.setAllowUpdateEvent(false);
        }
    }

    @Override
    public boolean hasFound() {
        return ReflectionsUtil.hasAnnotationClassLoaderSafe(clazz, clazz, CrudEvents.class);
    }

    @Override
    public TrackingDto getProcessingResult() {
        return trackingDto;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public void setTrackingDto(TrackingDto trackingDto) {
        this.trackingDto = trackingDto;
    }
}
