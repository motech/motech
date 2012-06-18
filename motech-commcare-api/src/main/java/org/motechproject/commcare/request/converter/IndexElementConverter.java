package org.motechproject.commcare.request.converter;

import org.motechproject.commcare.request.IndexElement;
import org.motechproject.commcare.request.IndexSubElement;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class IndexElementConverter implements Converter{

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext marshallingContext) {

        IndexElement element = (IndexElement) o;

        for (IndexSubElement subElement : element.getSubElements()) {
            writer.startNode(subElement.getIndexNodeName());
            writer.addAttribute("case_type", subElement.getCase_type());
            writer.setValue(subElement.getCase_id());
            writer.endNode();
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {
        return null;
    }

    @Override
    public boolean canConvert(Class aClass) {
        return aClass.equals(IndexElement.class);
    }
}