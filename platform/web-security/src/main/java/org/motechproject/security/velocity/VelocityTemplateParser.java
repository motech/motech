package org.motechproject.security.velocity;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.motechproject.security.ex.VelocityTemplateParsingException;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

/**
 * Responsible for loading and merging velocity templates stored as raw config files.
 */
@Component
public class VelocityTemplateParser {

    private RuntimeServices rs = RuntimeSingleton.getRuntimeServices();
    private SettingsFacade settingsFacade;

    public String mergeTemplateIntoString(String templateFilename, Map<String, Object> params)
            throws VelocityTemplateParsingException {
        try (InputStream is = settingsFacade.getRawConfig(templateFilename)) {
            StringReader reader = new StringReader(IOUtils.toString(is));
            SimpleNode node = rs.parse(reader, templateFilename);

            Template template = new Template();
            template.setRuntimeServices(rs);
            template.setData(node);
            template.initDocument();

            StringWriter writer = new StringWriter();
            template.merge(new VelocityContext(params), writer);
            return writer.toString();
        } catch (ParseException|IOException e) {
            throw new VelocityTemplateParsingException("Couldn't merge template into string", e);
        }
    }

    @Autowired
    public void setSettingsFacade(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }
}
