package org.motechproject.commcare.request.converter;

import org.motechproject.commcare.request.CloseElement;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class CloseElementConverter implements Converter {

    @Override
    public boolean canConvert(Class aClass) {
        return aClass.equals(CloseElement.class);
    }

    @Override
    public void marshal(Object arg0, HierarchicalStreamWriter arg1,
            MarshallingContext arg2) {

    }

    @Override
    public Object unmarshal(HierarchicalStreamReader arg0,
            UnmarshallingContext arg1) {
        return null;
    }

}
