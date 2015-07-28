package org.motechproject.mds.service.impl;

import org.motechproject.mds.docs.RestDocumentationGenerator;
import org.motechproject.mds.service.RestDocumentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Writer;
import java.util.Locale;

/**
 * Implementation of {@link org.motechproject.mds.service.RestDocumentationService}
 */
@Service("restDocumentationServiceImpl")
public class RestDocumentationServiceImpl implements RestDocumentationService {

    @Autowired
    private RestDocumentationGenerator docGenerator;

    @Override
    @Transactional
    public void retrieveDocumentation(Writer writer, String serverPrefix, Locale locale) {
        docGenerator.generateDocumentation(writer, serverPrefix, locale);
    }
}
