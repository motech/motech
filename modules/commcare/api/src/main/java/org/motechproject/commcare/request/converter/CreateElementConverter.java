package org.motechproject.commcare.request.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.motechproject.commcare.exception.MalformedCaseXmlException;
import org.motechproject.commcare.request.CreateElement;

public class CreateElementConverter implements Converter {
    public boolean canConvert(Class clazz) {
        return clazz.equals(CreateElement.class);
    }

    public void marshal(Object o, HierarchicalStreamWriter writer,
            MarshallingContext marshallingContext) {
        CreateElement element = (CreateElement) o;

        if (element.getCaseType() == null || element.getCaseName() == null || element.getCaseType().trim().length() == 0 || element.getCaseName().trim().length() == 0) {
            throw new MalformedCaseXmlException("Case xml is missing its case type or case name");
        }

        writer.startNode("case_type");
        writer.setValue(element.getCaseType());
        writer.endNode();

        if (element.getOwnerId() != null) {
            writer.startNode("owner_id");
            writer.setValue(element.getOwnerId());
            writer.endNode();
        }

        writer.startNode("case_name");
        writer.setValue(element.getCaseName());
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader,
            UnmarshallingContext unmarshallingContext) {
        return null;
    }
}
