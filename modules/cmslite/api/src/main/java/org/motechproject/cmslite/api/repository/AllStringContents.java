package org.motechproject.cmslite.api.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.StringContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View(name = "by_language_and_name", map = "function(doc) { if (doc.type === 'StringContent') emit([doc.language, doc.name], doc); }")
public class AllStringContents extends BaseContentRepository<StringContent> {
    @Autowired
    protected AllStringContents(@Qualifier("cmsLiteDatabase") CouchDbConnector db) {
        super(StringContent.class, db);
    }

    @Override
    public StringContent getContent(String language, String name) {
        List<StringContent> result = queryView("by_language_and_name", ComplexKey.of(language, name));
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public boolean isContentAvailable(String language, String name) {
        return !queryView("by_language_and_name", ComplexKey.of(language, name)).isEmpty();
    }

    @Override
    public void addContent(StringContent content) throws CMSLiteException {
        StringContent contentFromDB = getContent(content.getLanguage(), content.getName());

        if (contentFromDB != null) {
            contentFromDB.setValue(content.getValue());
            update(contentFromDB);
        } else {
            add(content);
        }
    }
}
