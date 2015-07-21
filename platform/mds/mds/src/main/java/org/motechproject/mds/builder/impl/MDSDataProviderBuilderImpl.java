package org.motechproject.mds.builder.impl;

import org.apache.velocity.app.VelocityEngine;
import org.motechproject.mds.builder.MDSDataProviderBuilder;
import org.motechproject.mds.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.annotation.Resource;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for generating MDS Data Provider json.
 */
@Component("mdsDataProviderBuilder")
public class MDSDataProviderBuilderImpl implements MDSDataProviderBuilder {

    private static final String MDS_TASK_DATA_PROVIDER = "/velocity/templates/task-data-provider.vm";

    private EntityService entityService;
    private VelocityEngine velocityEngine;

    @Override
    public String generateDataProvider() {
        Map<String, Object> model = new HashMap<>();
        model.put("service", entityService);
        StringWriter writer = new StringWriter();

        VelocityEngineUtils.mergeTemplate(velocityEngine, MDS_TASK_DATA_PROVIDER, model, writer);

        return writer.toString();
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    @Resource(name = "mdsVelocityEngine")
    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public EntityService getEntityService() {
        return entityService;
    }
}
