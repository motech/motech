package org.motechproject.mds.repository;


import org.motechproject.mds.domain.Type;
import org.motechproject.mds.domain.TypeValidation;
import org.motechproject.mds.util.QueryUtil;
import org.springframework.stereotype.Repository;

import javax.jdo.Query;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

/**
 * The <code>AllTypeValidations</code> class is a repository class that operates on instances of
 * {@link org.motechproject.mds.domain.TypeValidation}.
 */
@Repository
public class AllTypeValidations extends MotechDataRepository<TypeValidation> {

    public AllTypeValidations() {
        super(TypeValidation.class);
    }

    public List<TypeValidation> retrieveAll(Type type, Class<? extends Annotation> annotation) {
        String filter = QueryUtil.createFilter(new String[]{"valueType"});
        filter += "&& annotations.contains(param1)";

        Query query = getPersistenceManager().newQuery(TypeValidation.class);
        query.setFilter(filter);
        query.declareParameters(QueryUtil.createDeclareParameters(new Object[]{type, annotation}));

        Collection collection = (Collection) query.execute(type, annotation);

        return cast(collection);
    }

}
