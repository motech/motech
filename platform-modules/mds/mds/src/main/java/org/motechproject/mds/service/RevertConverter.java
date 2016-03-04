package org.motechproject.mds.service;

import org.motechproject.mds.domain.RelationshipHolder;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.entityinfo.EntityInfo;
import org.motechproject.mds.entityinfo.FieldInfo;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
import org.springframework.context.ApplicationContext;

import java.beans.PropertyDescriptor;
import java.util.Collection;

/**
 * An implementation of {@link org.motechproject.mds.util.PropertyUtil.ValueConverter}.
 */
public class RevertConverter implements PropertyUtil.ValueConverter {

    private final EntityInfo entityInfo;
    private final ApplicationContext applicationContext;

    public RevertConverter(EntityInfo entityInfo, ApplicationContext applicationContext) {
        this.entityInfo = entityInfo;
        this.applicationContext = applicationContext;
    }

    @Override
    public Object convert(Object value, PropertyDescriptor descriptor) {
        FieldInfo field = entityInfo.getField(descriptor.getName());
        FieldDto fieldDto = field.getField();
        if (value == null || field == null || !fieldDto.getType().isRelationship()) {
            return value;
        } else {
            RelationshipHolder relHolder = new RelationshipHolder(fieldDto);
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
