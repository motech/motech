package org.motechproject.mds.jdo;

import org.apache.commons.lang.ArrayUtils;
import org.datanucleus.api.jdo.metadata.JDOAnnotationReader;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.annotations.AnnotationObject;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.domain.MdsEntity;
import org.motechproject.mds.domain.MdsVersionedEntity;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.util.Constants;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import java.util.HashMap;

/**
 * MDS JDO annotation reader, extends the regular {@link org.datanucleus.api.jdo.metadata.JDOAnnotationReader}
 * This class was introduced because {@link org.datanucleus.api.jdo.metadata.JDOAnnotationReader} would not read
 * field annotations for metadata if there was no class level JDO annotations. This extension will recognize the
 * {@link org.motechproject.mds.annotations.Entity} annotation as an annotation indicating that the class is
 * persistence capable.
 */
public class MdsJdoAnnotationReader extends JDOAnnotationReader {

    public MdsJdoAnnotationReader(MetaDataManager mgr) {
        super(mgr);

        // add the MDS annotations package to the supported packages
        String[] supportedAnnotations = (String[]) ArrayUtils.add(super.getSupportedAnnotationPackages(),
                "org.motechproject.mds.annotations");
        setSupportedAnnotationPackages(supportedAnnotations);
    }

    @Override
    protected AnnotationObject isClassPersistable(Class cls) {
        AnnotationObject annotationObject = super.isClassPersistable(cls);

        // if super does not recognize this object as PC, then try looking for the Entity annotation
        if (annotationObject == null && ReflectionsUtil.hasAnnotation(cls, Entity.class)) {
            // default params
            HashMap<String, Object> annotationParams = new HashMap<>();
            annotationParams.put("identityType", IdentityType.DATASTORE);

            Class superClass = cls.getSuperclass();
            while (superClass != null) {
                if (superClass == MdsEntity.class || superClass == MdsVersionedEntity.class) {
                    annotationParams.put("identityType", IdentityType.APPLICATION);
                }
                superClass = superClass.getSuperclass();
            }

            annotationParams.put("detachable", Constants.Util.TRUE);

            // we fake a persistence capable annotation
            annotationObject = new AnnotationObject(PersistenceCapable.class.getName(), annotationParams);
        }

        return annotationObject;
    }
}
