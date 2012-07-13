package org.motechproject.commcare.request.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.motechproject.commcare.request.CloseElement;

public class CloseElementConverter implements Converter {
    public boolean canConvert(Class aClass) {
        return aClass.equals(CloseElement.class);
    }

    public void marshal(Object arg0, HierarchicalStreamWriter arg1,
            MarshallingContext arg2) {
    }

    public Object unmarshal(HierarchicalStreamReader arg0,
            UnmarshallingContext arg1) {
        return null;
    }
}
