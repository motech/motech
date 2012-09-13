package org.motechproject.commcare.request.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.motechproject.commcare.domain.UpdateTask;

import java.util.Map;

public class UpdateElementConverter implements Converter {
    public boolean canConvert(Class clazz) {
        return clazz.equals(UpdateTask.class);
    }

    public void marshal(Object o, HierarchicalStreamWriter writer,
            MarshallingContext marshallingContext) {
        UpdateTask element = (UpdateTask) o;

        if (element.getCaseType() != null) {
            writer.startNode("case_type");
            writer.setValue(element.getCaseType());
            writer.endNode();
        }

        if (element.getCaseName() != null) {
            writer.startNode("case_name");
            writer.setValue(element.getCaseName());
            writer.endNode();
        }

        if (element.getDateOpened() != null) {
            writer.startNode("date_opened");
            writer.setValue(element.getDateOpened());
            writer.endNode();
        }

        if (element.getOwnerId() != null) {
            writer.startNode("owner_id");
            writer.setValue(element.getOwnerId());
            writer.endNode();
        }

        Map<String, String> fieldValues = element.getFieldValues();

        if (fieldValues != null) {
            for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                writer.startNode((String) entry.getKey());
                writer.setValue((String) entry.getValue());
                writer.endNode();
            }
        }
    }

    public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader,
            UnmarshallingContext unmarshallingContext) {
        return null;
    }
}
