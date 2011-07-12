package org.motechproject.cmslite.api.dao.impl;

import org.ektorp.AttachmentInputStream;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.cmslite.api.ResourceQuery;
import org.motechproject.cmslite.api.dao.CMSLiteDAO;
import org.motechproject.cmslite.api.model.Resource;
import org.motechproject.dao.MotechAuditableRepository;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class CMSLiteDAOImpl extends MotechAuditableRepository<Resource> implements CMSLiteDAO {

    protected CMSLiteDAOImpl(@Qualifier("cmsLiteDatabase") CouchDbConnector db) {
        super(Resource.class, db);
        initStandardDesignDocument();
    }

    @View(name = "by_name_and_language", map = "function(doc) { if (doc.type=='RESOURCE') { emit([doc.name, doc.language], doc); } }")
    @Override
    public Resource getResource(ResourceQuery resourceQuery) {
        ViewQuery query = new ViewQuery().designDocId("_design/Resource").viewName("by_name_and_language").key(ComplexKey.of(resourceQuery.getName(), resourceQuery.getLanguage()));
        List<Resource> result = db.queryView(query, Resource.class);

        if (result == null || result.isEmpty()) return null;

        Resource fetchedResource = result.get(0);
        AttachmentInputStream attachmentInputStream = db.getAttachment(fetchedResource.getId(), fetchedResource.getId());
        fetchedResource.setInputStream(attachmentInputStream);
        return fetchedResource;
    }
}
