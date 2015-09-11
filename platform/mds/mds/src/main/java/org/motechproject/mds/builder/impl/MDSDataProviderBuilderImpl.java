package org.motechproject.mds.builder.impl;

import org.apache.velocity.app.VelocityEngine;
import org.motechproject.mds.annotations.internal.AnnotationProcessingContext;
import org.motechproject.mds.builder.MDSDataProviderBuilder;
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

    private VelocityEngine velocityEngine;

    @Override
    public String generateDataProvider(AnnotationProcessingContext context) {
        Map<String, Object> model = new HashMap<>();
        model.put("entities", context.getAllEntities());
        StringWriter writer = new StringWriter();

        VelocityEngineUtils.mergeTemplate(velocityEngine, MDS_TASK_DATA_PROVIDER, model, writer);

        return writer.toString();
    }


    @Resource(name = "mdsVelocityEngine")
    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }
}
