package org.motechproject.mds.service.impl;

import org.apache.commons.io.IOUtils;
import org.motechproject.mds.domain.RestDocs;
import org.motechproject.mds.ex.DocumentationAccessException;
import org.motechproject.mds.repository.RestDocsRepository;
import org.motechproject.mds.service.RestDocumentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.SQLException;

/**
 * Implementation of {@link org.motechproject.mds.service.RestDocumentationService}
 * The REST documentation is retrieved from {@link org.motechproject.mds.repository.RestDocsRepository}.
 *
 */
@Service("restDocumentationServiceImpl")
public class RestDocumentationServiceImpl implements RestDocumentationService {

    @Autowired
    private RestDocsRepository restDocsRepository;

    /**
     * {@inheritDoc}
     * @param writer The output for the documentation.
     */
    @Override
    @Transactional
    public void retrieveDocumentation(Writer writer) {
        RestDocs restDocs = restDocsRepository.getRestDocs();
        if (restDocs == null) {
            throw new IllegalStateException("No REST documentation available in the database");
        }

        try (InputStream in = restDocs.getDocumentation().getAsciiStream()) {
            IOUtils.copy(in, writer);
        } catch (SQLException | IOException e) {
            throw new DocumentationAccessException(e);
        }
    }
}
