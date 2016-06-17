package org.motechproject.mds.service;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.RelationshipHolder;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
import org.springframework.context.ApplicationContext;

import java.beans.PropertyDescriptor;
import java.util.Collection;

/**
 * An implementation of {@link org.motechproject.mds.util.PropertyUtil.ValueConverter}.
 */
public class RevertConverter implements PropertyUtil.ValueConverter {

    private final Entity entity;
    private final ApplicationContext applicationContext;

    public RevertConverter(Entity entity, ApplicationContext applicationContext) {
        this.entity = entity;
        this.applicationContext = applicationContext;
    }

    @Override
    public Object convert(Object value, PropertyDescriptor descriptor) {
        Field field = entity.getField(descriptor.getName());
        if (value == null || field == null || !field.getType().isRelationship()) {
            return value;
        } else {
            RelationshipHolder relHolder = new RelationshipHolder(field);
            String relatedClass = relHolder.getRelatedClass();

            MotechDataService relatedDataService = ServiceUtil.getServiceFromAppContext(applicationContext,
                    relatedClass);

            if (value instanceof Collection) {
                Collection<Long> idColl = (Collection<Long>) value;
                return idCollToRelColl(idColl, relatedDataService);
            } else {
                Long id = (Long) value;
                return relatedDataService.findById(id);
            }
        }
    }

    private Collection idCollToRelColl(Collection<Long> ids, MotechDataService relatedService) {
        Collection relColl = TypeHelper.suggestAndCreateCollectionImplementation(ids.getClass());
        for (Long id : ids) {
            Object relatedInstance = relatedService.findById(id);
            if (relatedInstance != null) {
                relColl.add(relatedInstance);
            }
        }
        return relColl;
    }
}
