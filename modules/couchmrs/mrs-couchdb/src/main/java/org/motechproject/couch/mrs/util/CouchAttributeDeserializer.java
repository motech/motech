package org.motechproject.couch.mrs.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.motechproject.couch.mrs.model.CouchAttribute;
import org.motechproject.mrs.domain.Attribute;

public class CouchAttributeDeserializer extends JsonDeserializer<List<Attribute>> {

    @Override
    public List<Attribute> deserialize(JsonParser jsonParser, DeserializationContext arg1) throws IOException,
            JsonProcessingException {

        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        Iterator<JsonNode> nodes = node.getElements();

        List<Attribute> attributes = new ArrayList<Attribute>();

        while (nodes.hasNext()) {

            JsonNode localnode = nodes.next();

            String name = localnode.get("name").getTextValue();

            String value = localnode.get("value").getTextValue();

            Attribute couchAttribute = new CouchAttribute();

            couchAttribute.setName(name);
            couchAttribute.setValue(value);

            attributes.add(couchAttribute);

        }

        return attributes;
    }

}
