package org.motechproject.mds.service.impl;

import org.motechproject.mds.docs.RestDocumentationGenerator;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.RestDocumentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Writer;
import java.util.List;

/**
 * Implementation of {@link org.motechproject.mds.service.RestDocumentationService}
 */
@Service("restDocumentationServiceImpl")
public class RestDocumentationServiceImpl implements RestDocumentationService {

    @Autowired
    private RestDocumentationGenerator docGenerator;

    @Autowired
    private AllEntities allEntities;

    @Override
    @Transactional
    public void retrieveDocumentation(Writer writer, String serverPrefix) {
        List<Entity> entities = allEntities.retrieveAll();
        docGenerator.generateDocumentation(writer, entities, serverPrefix);
    }
}
