package org.motechproject.mds.domain.relationships;


import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldMetadata;
import org.motechproject.mds.util.Constants;

/**
 * Created by pawel on 5/20/14.
 */
public class Relationship {

    public String getFieldType(Field field) {
        return getRelatedClassName(field);
    }

    public String getGenericSignature(Field field) {
        return null;
    }

    protected String getRelatedClassName(Field field) {
        FieldMetadata fmd = field.getMetadata(Constants.MetadataKeys.RELATED_CLASS);

        if (fmd == null) {
            throw new IllegalStateException(String.format("Unknown type for relationship %s in entity %s",
                    field.getName(), field.getEntity().getName()));
        }

        return fmd.getValue();
    }
}
