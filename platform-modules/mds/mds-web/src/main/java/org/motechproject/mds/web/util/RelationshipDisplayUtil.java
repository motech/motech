package org.motechproject.mds.web.util;

import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.PropertyUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * The <code>RelationshipDisplayUtil</code> class contains helper methods,
 * responsible for parsing and adjusting the objects to be suitable for view on UI.
 */
@Component
public class RelationshipDisplayUtil {

    /**
     * Parses the passed Java object, by setting any relationship fields to null. This is used
     * to avoid exceptions for possible lazy loaded fields accessed outside of the transaction.
     * The relationship fields are discovered based on the field metadata.
     * Invoking this method in a transaction will fail and throw an exception.
     *
     * @param value java object or a collection of objects to remove relationship from
     * @param fields field definitions of the given object
     * @return java object or a collection of objects with relationship fields set to null
     */
    @Transactional(propagation = Propagation.NEVER)
    public Object breakDeepRelationChainForDisplay(Object value, List<FieldDto> fields) {
        boolean isCollection = value instanceof Collection;

        // Set any relationship fields to null
        for (FieldDto fieldDto : fields) {
            if (fieldDto.getMetadata(Constants.MetadataKeys.RELATED_CLASS) != null && isCollection) {
                for (Object instance : (Collection) value) {
                    PropertyUtil.safeSetProperty(instance, fieldDto.getBasic().getName(), null);
                }
            } else if (fieldDto.getMetadata(Constants.MetadataKeys.RELATED_CLASS) != null && !isCollection) {
                PropertyUtil.safeSetProperty(value, fieldDto.getBasic().getName(), null);
            }
        }

        return value;
    }

}
