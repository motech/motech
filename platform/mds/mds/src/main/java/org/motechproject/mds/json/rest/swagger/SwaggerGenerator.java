package org.motechproject.mds.json.rest.swagger;

import com.google.gson.Gson;
import org.motechproject.mds.json.rest.RestDocumentationGenerator;
import org.motechproject.mds.json.rest.RestEntry;
import org.motechproject.mds.json.rest.swagger.model.Info;
import org.motechproject.mds.json.rest.swagger.model.SwaggerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * A REST API documentation generator for Swagger - http://swagger.io/.
 * Generates a resources file in the Swagger JSON format.
 */
@Service
public class SwaggerGenerator implements RestDocumentationGenerator {

    private static final String V_2 = "2.0";

    @Autowired
    @Qualifier("restDocumentationProperties")
    private Properties properties;

    @Override
    public void generateDocumentation(PrintWriter writer, List<RestEntry> restEntries) {
        Gson gson = new Gson();

        SwaggerModel swaggerModel = new SwaggerModel();
        swaggerModel.setSwagger(V_2);
        swaggerModel.setInfo(motechInfo());

        swaggerModel.setBasePath("/module");

        swaggerModel.setSchemes(Arrays.asList("http"));
        swaggerModel.setProduces(producesJson());
        swaggerModel.setConsumes(producesJson());

        for (RestEntry restEntry : restEntries) {

        }

        gson.toJson(swaggerModel, writer);
    }



    private Info motechInfo() {
        Info info = new Info();

        // TODO: build Info

        return info;
    }

    private List<String> producesJson() {
        return Arrays.asList(MediaType.APPLICATION_JSON_VALUE);
    }
}
