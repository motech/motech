package org.motechproject.mds.testutil;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldMetadata;
import org.motechproject.mds.domain.FieldSetting;
import org.motechproject.mds.domain.FieldValidation;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.domain.RestOptions;
import org.motechproject.mds.domain.Tracking;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.domain.TypeSetting;
import org.motechproject.mds.domain.TypeValidation;
import org.motechproject.mds.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EntitySchemaBuilder {

    private String clazz;
    private String module;
    private String namespace;
    private List<FieldSchemaBuilder> fields = new ArrayList<>();
    private List<LookupSchemaBuilder> lookups = new ArrayList<>();
    private BrowsingSchemaBuilder browsing = new BrowsingSchemaBuilder();
    private RestSchemaBuilder rest = new RestSchemaBuilder();
    private AuditingSchemaBuilder auditing = new AuditingSchemaBuilder();

    public EntitySchemaBuilder() {
    }

    public static EntitySchemaBuilder dde(String clazz, String module, String namespace) {
        EntitySchemaBuilder builder = new EntitySchemaBuilder();
        builder.clazz = clazz;
        builder.module = module;
        builder.namespace = namespace;
        return builder;
    }

    public static EntitySchemaBuilder eude(String clazz) {
        EntitySchemaBuilder builder = new EntitySchemaBuilder();
        builder.clazz = clazz;
        return builder;
    }

    public FieldSchemaBuilder field(String name, String type) {
        FieldSchemaBuilder builder = new FieldSchemaBuilder(name, type);
        fields.add(builder);
        return builder;
    }

    public FieldSchemaBuilder field(String name, String displayName, String type) {
        FieldSchemaBuilder builder = new FieldSchemaBuilder(name, displayName, type);
        fields.add(builder);
        return builder;
    }

    public LookupSchemaBuilder lookup(String name) {
        LookupSchemaBuilder builder = new LookupSchemaBuilder(name);
        lookups.add(builder);
        return builder;
    }

    public BrowsingSchemaBuilder browsing() {
        return browsing;
    }

    public RestSchemaBuilder rest() {
        return rest;
    }

    public AuditingSchemaBuilder auditing() {
        return auditing;
    }

    public Entity build() {
        Entity entity = new Entity(clazz, module, namespace, null);

        entity.setRestOptions(rest.build());
        entity.getRestOptions().setEntity(entity);

        entity.setTracking(auditing.build());
        entity.getTracking().setEntity(entity);

        Map<String, Field> fieldsMap = new HashMap<>();

        for (FieldSchemaBuilder fieldBuilder : fields) {
            Field field = fieldBuilder.build();
            field.setEntity(entity);
            field.setExposedViaRest(rest.fields.contains(field.getName()));
            field.setUIDisplayable(browsing.fields.contains(field.getName()));
            field.setUIDisplayPosition(Integer.valueOf(browsing.fields.indexOf(field.getName())).longValue());
            field.setUIFilterable(browsing.filters.contains(field.getName()));
            fieldsMap.put(field.getName(), field);
            entity.addField(field);
        }

        for (LookupSchemaBuilder lookupBuilder : lookups) {
            Lookup lookup = lookupBuilder.build();
            lookup.setEntity(entity);
            lookup.setExposedViaRest(rest.lookups.contains(lookup.getLookupName()));
            setLookupFields(lookup, lookupBuilder, fieldsMap);
            entity.addLookup(lookup);
        }

        return entity;
    }

    private void setLookupFields(Lookup lookup, LookupSchemaBuilder lookupBuilder, Map<String, Field> fieldsMap) {
        List<Field> lookupFields = getLookupFields(lookupBuilder.fields, fieldsMap);
        lookup.setFields(lookupFields);
        for (Field lookupField : lookupFields) {
            lookupField.getLookups().add(lookup);
        }
    }

    private List<Field> getLookupFields(List<String> fields, Map<String, Field> fieldsMap) {
        List<Field> lookupFields = new ArrayList<>(fields.size());
        for (String field : fields) {
            Field lookupField = fieldsMap.get(field);
            if (null != lookupField) {
                lookupFields.add(lookupField);
            }
        }
        return lookupFields;
    }

    public class BrowsingSchemaBuilder {
        private List<String> fields = new ArrayList<>();
        private List<String> filters = new ArrayList<>();

        public BrowsingSchemaBuilder fields(String... fields) {
            this.fields = Arrays.asList(fields);
            return this;
        }

        public BrowsingSchemaBuilder filters(String... filters) {
            this.filters = Arrays.asList(filters);
            return this;
        }

        public EntitySchemaBuilder done() {
            return EntitySchemaBuilder.this;
        }
    }

    public class RestSchemaBuilder {
        private List<String> fields = new ArrayList<>();
        private List<String> lookups = new ArrayList<>();
        private boolean create;
        private boolean read;
        private boolean update;
        private boolean delete;

        public RestSchemaBuilder fields(String... fields) {
            this.fields = Arrays.asList(fields);
            return this;
        }

        public RestSchemaBuilder lookups(String... lookups) {
            this.lookups = Arrays.asList(lookups);
            return this;
        }

        public RestSchemaBuilder crud(boolean create, boolean read, boolean update, boolean delete) {
            this.create = create;
            this.read = read;
            this.update = update;
            this.delete = delete;
            return this;
        }

        public RestOptions build() {
            RestOptions rest = new RestOptions();
            rest.setAllowCreate(create);
            rest.setAllowRead(read);
            rest.setAllowUpdate(update);
            rest.setAllowDelete(delete);
            return rest;
        }

        public EntitySchemaBuilder done() {
            return EntitySchemaBuilder.this;
        }
    }

    public class AuditingSchemaBuilder {
        private boolean recordHistory;
        private boolean create;
        private boolean update;
        private boolean delete;

        public AuditingSchemaBuilder recordHistory(boolean recordHistory) {
            this.recordHistory = recordHistory;
            return this;
        }

        public AuditingSchemaBuilder events(boolean create, boolean update, boolean delete) {
            this.create = create;
            this.update = update;
            this.delete = delete;
            return this;
        }

        public Tracking build() {
            Tracking tracking = new Tracking();
            tracking.setRecordHistory(recordHistory);
            tracking.setAllowCreateEvent(create);
            tracking.setAllowUpdateEvent(update);
            tracking.setAllowDeleteEvent(delete);
            return tracking;
        }

        public EntitySchemaBuilder done() {
            return EntitySchemaBuilder.this;
        }
    }

    public class FieldSchemaBuilder {
        private String name;
        private String displayName;
        private boolean required;
        private boolean readOnly;
        private Type type;
        private String defaultValue;
        private String tooltip;
        private String placeholder;
        private List<FieldMetadata> metadata = new ArrayList<>();
        private List<FieldValidation> validations = new ArrayList<>();
        private List<FieldSetting> settings = new ArrayList<>();

        public FieldSchemaBuilder(String name, String type) {
            this(name, name, type);
        }

        public FieldSchemaBuilder(String name, String displayName, String type) {
            this.name = name;
            this.displayName = displayName;
            this.type = TypeResolver.resolve(type);
        }

        public Field build() {
            Field field = new Field(null, name, displayName, type, required, readOnly);
            field.setDefaultValue(defaultValue);
            field.setTooltip(tooltip);
            field.setPlaceholder(placeholder);
            for (FieldMetadata fieldMetadata : metadata) {
                field.addMetadata(new FieldMetadata(field, fieldMetadata.getKey(), fieldMetadata.getValue()));
            }
            for (FieldValidation fieldValidation : validations) {
                field.addValidation(new FieldValidation(field, fieldValidation.getDetails(), fieldValidation.getValue(), fieldValidation.isEnabled()));
            }
            for (FieldSetting fieldSetting : settings) {
                field.addSetting(new FieldSetting(field, fieldSetting.getDetails(), fieldSetting.getValue()));
            }
            return field;
        }

        public FieldSchemaBuilder required(boolean required) {
            this.required = required;
            return this;
        }

        public FieldSchemaBuilder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        public FieldSchemaBuilder autoGenerated() {
            return metadata(Constants.Util.AUTO_GENERATED, Constants.Util.TRUE).readOnly(true);
        }

        public FieldSchemaBuilder relatedClass(String relatedClass) {
            return metadata(Constants.MetadataKeys.RELATED_CLASS, relatedClass);
        }

        public FieldSchemaBuilder relatedField(String relatedFields) {
            return metadata(Constants.MetadataKeys.RELATED_FIELD, relatedFields);
        }

        public FieldSchemaBuilder metadata(String key, String value) {
            metadata.add(new FieldMetadata(null, key, value));
            return this;
        }

        public FieldSchemaBuilder validation(String key, String value, boolean enabled) {
            validations.add(new FieldValidation(null, new TypeValidation(key, type), value, enabled));
            return this;
        }

        public FieldSchemaBuilder setting(String key, String value) {
            settings.add(new FieldSetting(null, new TypeSetting(key), value));
            return this;
        }

        public FieldSchemaBuilder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public FieldSchemaBuilder tooltip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public FieldSchemaBuilder placeholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public EntitySchemaBuilder done() {
            return EntitySchemaBuilder.this;
        }
    }

    public class LookupSchemaBuilder {
        private String name;
        private boolean singleObjectReturn;
        private String methodName;
        private boolean readOnly;
        private List<String> fields = new ArrayList<>();

        public LookupSchemaBuilder(String name) {
            this.name = name;
            this.methodName = name;
        }

        public LookupSchemaBuilder singleObjectReturn(boolean singleObjectReturn) {
            this.singleObjectReturn = singleObjectReturn;
            return this;
        }

        public LookupSchemaBuilder methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public LookupSchemaBuilder fields(String... fields) {
            this.fields = Arrays.asList(fields);
            return this;
        }

        public LookupSchemaBuilder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        public Lookup build() {
            Lookup lookup = new Lookup();
            lookup.setLookupName(name);
            lookup.setSingleObjectReturn(singleObjectReturn);
            lookup.setMethodName(methodName);
            lookup.setReadOnly(readOnly);
            return lookup;
        }

        public EntitySchemaBuilder done() {
            return EntitySchemaBuilder.this;
        }
    }
}
