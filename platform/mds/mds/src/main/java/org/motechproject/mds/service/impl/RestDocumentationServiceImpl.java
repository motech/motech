package org.motechproject.mds.service.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.domain.RestDocs;
import org.motechproject.mds.ex.DocumentationAccessException;
import org.motechproject.mds.repository.RestDocsRepository;
import org.motechproject.mds.service.RestDocumentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Writer;

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
    public void retrieveDocumentation(Writer writer, String serverPrefix) {
        RestDocs restDocs = restDocsRepository.getRestDocs();
        if (restDocs == null) {
            throw new IllegalStateException("No REST documentation available in the database");
        }


        try {
            IOUtils.write(StringUtils.replace(restDocs.getDocumentation(), "${server.prefix}",
                            StringUtils.defaultString(serverPrefix)), writer);
        } catch (IOException e) {
            throw new DocumentationAccessException(e);
        }
    }
}
