package org.motechproject.mds.json;

import com.google.gson.stream.JsonReader;
import org.apache.commons.lang.ArrayUtils;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.RelationshipHolder;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Base64;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The <code>InstancesReader</code> class is a wrapper for JsonReader that provides methods to deserialize entity
 * instances from underlying reader that were previously written by InstancesWriter.
 *
 * @see org.motechproject.mds.domain.Entity
 * @see org.motechproject.mds.domain.Field
 * @see org.motechproject.mds.json.ObjectReader
 * @see com.google.gson.stream.JsonReader
 * @see org.motechproject.mds.json.InstancesWriter
 */
public class InstancesReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstancesReader.class);

    private JsonReader jsonReader;
    private Entity entity;
    private ImportContext importContext;
    private ObjectReader objectReader;
    private MotechDataService dataService;

    public InstancesReader(JsonReader jsonReader, Entity entity, ImportContext importContext) {
        this.jsonReader = jsonReader;
        this.entity = entity;
        this.importContext = importContext;
        this.dataService = importContext.getDataService(entity.getClassName());
        this.objectReader = new ObjectReader(jsonReader);
    }

    public boolean hasNext() throws IOException {
        return jsonReader.hasNext();
    }

    public void importInstance() throws IOException {
        try {
            Object instance = dataService.getClassType().newInstance();
            Long refId = null;
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String fieldName = objectReader.readName();
                if ("refId".equals(fieldName)) {
                    refId = jsonReader.nextLong();
                } else {
                    Field field = entity.getField(fieldName);
                    readProperty(instance, field);
                }
            }
            jsonReader.endObject();
            if (null != refId) {
                importContext.putInstanceOfEntity(entity.getClassName(), refId, dataService.create(instance));
            }
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("Exception occurred during importing instances", e);
        }
    }

    private void readProperty(Object instance, Field field) throws IOException {
        if (field.getType().isRelationship()) {
            readRelationshipProperty(field, instance);
        } else if (field.getType().isCombobox()) {
            readComboboxProperty(field, instance);
        } else if (field.getType().isMap()) {
            readMapProperty(field, instance);
        } else if (field.getType().isBlob()) {
            readBlobProperty(field, instance);
        } else {
            readPlainProperty(field, instance);
        }
    }

    private void readPlainProperty(Field field, Object instance) throws IOException {
        Object property = objectReader.readObject(field.getType().getTypeClass());
        PropertyUtil.safeSetProperty(instance, field.getName(), property);
    }

    private void readBlobProperty(Field field, Object instance) throws IOException {
        if (!objectReader.isNextNull()) {
            byte[] blob = Base64.decode(objectReader.readString().getBytes());
            Class<?> blobType = PropertyUtil.safeGetPropertyType(instance, field.getName());
            if (blobType.isAssignableFrom(byte[].class)) {
                PropertyUtil.safeSetProperty(instance, field.getName(), blob);
            } else if (blobType.isAssignableFrom(Byte[].class)) {
                PropertyUtil.safeSetProperty(instance, field.getName(), ArrayUtils.toObject(blob));
            }
        }
    }

    private void readMapProperty(Field field, Object instance) throws IOException {
        Map<String, String> map = objectReader.readStringMap();
        PropertyUtil.safeSetProperty(instance, field.getName(), map);
    }

    private void readComboboxProperty(Field field, Object instance) throws IOException {
        ComboboxHolder comboboxHolder = new ComboboxHolder(field);
        Class<?> underlyingClass = getClassRelativeToObject(instance, comboboxHolder.getUnderlyingType());
        if (comboboxHolder.isAllowMultipleSelections()) {
            List<?> values = objectReader.readList(underlyingClass);
            PropertyUtil.safeSetCollectionProperty(instance, field.getName(), values);
        } else {
            Object value = objectReader.readObject(underlyingClass);
            PropertyUtil.safeSetProperty(instance, field.getName(), value);
        }
    }

    private Class<?> getClassRelativeToObject(Object instance, String underlyingType) {
        try {
            return instance.getClass().getClassLoader().loadClass(underlyingType);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("No class of name " + underlyingType + " found relative to " + instance.getClass(), e);
        }
    }

    private void readRelationshipProperty(Field field, Object instance) throws IOException {
        RelationshipHolder relationshipHolder = new RelationshipHolder(field);
        if (relationshipHolder.isManyToOne() || relationshipHolder.isOneToOne()) {
            Long refId = objectReader.readLong();
            if (null != refId) {
                Object relatedInstance = importContext.getInstanceOfEntity(relationshipHolder.getRelatedClass(), refId);
                PropertyUtil.safeSetProperty(instance, field.getName(), relatedInstance);
            }
        } else if (relationshipHolder.isManyToMany() || relationshipHolder.isOneToMany()) {
            List<Long> refIds = objectReader.readLongArray();
            if (null != refIds) {
                Collection<Object> relatedInstances = importContext.getInstancesOfEntity(relationshipHolder.getRelatedClass(), refIds);
                PropertyUtil.safeSetCollectionProperty(instance, field.getName(), relatedInstances);
            }
        }
    }
}
