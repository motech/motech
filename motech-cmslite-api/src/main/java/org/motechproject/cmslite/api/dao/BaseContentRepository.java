package org.motechproject.cmslite.api.dao;

import org.ektorp.CouchDbConnector;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.Content;
import org.motechproject.dao.MotechAuditableRepository;

public abstract class BaseContentRepository<T extends Content> extends MotechAuditableRepository<T> {
    protected BaseContentRepository(Class<T> type, CouchDbConnector db) {
        super(type, db);
    }

    public abstract T getContent(String language, String name);

    public abstract void addContent(T content) throws CMSLiteException;

    public abstract boolean isContentAvailable(String language, String name);
}
