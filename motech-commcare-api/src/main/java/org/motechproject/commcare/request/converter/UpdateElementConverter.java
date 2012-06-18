package org.motechproject.commcare.request.converter;

import java.util.Map;

import org.motechproject.commcare.request.UpdateElement;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class UpdateElementConverter implements Converter {

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(UpdateElement.class);
    }

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext marshallingContext) {

        UpdateElement element = (UpdateElement) o;

        if (element.getCase_type() != null) {
            writer.startNode("case_type");
            writer.setValue(element.getCase_type());
            writer.endNode();
        }

        if (element.getCase_name() != null) {
            writer.startNode("case_name");
            writer.setValue(element.getCase_name());
            writer.endNode();
        }

        if (element.getDate_opened() != null) {
            writer.startNode("date_opened");
            writer.setValue(element.getDate_opened());
            writer.endNode();
        }

        if (element.getOwner_id() != null) {
            writer.startNode("owner_id");
            writer.setValue(element.getOwner_id());
            writer.endNode();
        }

        Map<String, String> fieldValues = element.getFieldValues();

        for (Map.Entry<String, String> entry : fieldValues.entrySet())
        {
            writer.startNode(entry.getKey());
            writer.setValue(entry.getValue());
            writer.endNode();
        }

    }

    @Override
    public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader, 
            UnmarshallingContext unmarshallingContext) {
        return null;
    }

}
