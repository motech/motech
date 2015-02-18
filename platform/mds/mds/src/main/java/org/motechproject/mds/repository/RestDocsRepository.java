package org.motechproject.mds.repository;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mds.docs.RestDocumentationGenerator;
import org.motechproject.mds.domain.EntityInfo;
import org.motechproject.mds.domain.RestDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * Provides access to REST documentation. After the docs are generated, there
 * are stored in the database in CLOB form. Regenerating and accessing REST documentation
 * should be handled through this class.
 */
@Repository
public class RestDocsRepository extends MotechDataRepository<RestDocs> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestDocsRepository.class);

    @Autowired
    private RestDocumentationGenerator docGenerator;

    public RestDocsRepository() {
        super(RestDocs.class);
    }

    public void regenerateDocumentation(List<EntityInfo> entities) {
        RestDocs restDocs = getRestDocs();
        if (restDocs == null) {
            restDocs = create(new RestDocs());
        }

        try (StringWriter writer = new StringWriter()) {
            docGenerator.generateDocumentation(writer, entities);
            restDocs.setDocumentation(writer.toString());
        } catch (IOException e) {
            LOGGER.error("Unable to write REST documentation data do database", e);
        }

        update(restDocs);
    }

    public RestDocs getRestDocs() {
        List<RestDocs> allDocs = retrieveAll();
        return CollectionUtils.isNotEmpty(allDocs) ? allDocs.get(0) : null;
    }
}
