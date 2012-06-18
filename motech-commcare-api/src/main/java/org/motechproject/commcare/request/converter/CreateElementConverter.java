package org.motechproject.commcare.request.converter;

import org.motechproject.commcare.request.CreateElement;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class CreateElementConverter implements Converter {

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(CreateElement.class);
    }

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext marshallingContext) {

        CreateElement element = (CreateElement) o;

        writer.startNode("case_type");
        writer.setValue(element.getCase_type());
        writer.endNode();

        if (element.getOwner_id() != null) {
            writer.startNode("owner_id");
            writer.setValue(element.getOwner_id());
            writer.endNode();
        }

        writer.startNode("case_name");
        writer.setValue(element.getCase_name());
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {
        return null;
    }

}
