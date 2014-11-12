package org.motechproject.mds.jdo;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.annotations.AnnotationObject;
import org.datanucleus.metadata.annotations.MemberAnnotationHandler;

/**
 * The <code>MdsIgnoreAnnotationHandler</code> provides a mechanism to handle entity fields
 * annotated with @Ignore at the DataNucleus level, so that there is no database column
 * created for that field.
 *
 * @see org.motechproject.mds.annotations.Ignore
 */
public class MdsIgnoreAnnotationHandler implements MemberAnnotationHandler {

    // Note that this method gets called only for annotated properties and getters, but not setters.
    // It's due to DataNucleus internals.
    @Override
    public void processMemberAnnotation(AnnotationObject annotation, AbstractMemberMetaData mmd, ClassLoaderResolver clr) {
        mmd.setNotPersistent();
    }
}
