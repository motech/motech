package org.motechproject.mds.domain.relationships;

import org.motechproject.mds.domain.Field;
import org.motechproject.mds.javassist.JavassistHelper;

import java.util.List;

/**
 * Created by pawel on 5/22/14.
 */
public class OneToManyRelationship extends Relationship {

    @Override
    public String getFieldType(Field field) {
        return List.class.getName();
    }

    @Override
    public String getGenericSignature(Field field) {
        return JavassistHelper.genericSignature(List.class, getRelatedClassName(field));
    }
}
