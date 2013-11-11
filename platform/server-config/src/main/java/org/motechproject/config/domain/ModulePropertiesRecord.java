package org.motechproject.config.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * The <code>ModulePropertiesRecord</code> class represents a database record of a certain
 * module properties.
 */
@TypeDiscriminator("doc.type === 'ModulePropertiesRecord'")
public class ModulePropertiesRecord extends MotechBaseDataObject {

    private static final long serialVersionUID = -2184859902798932902L;
    private Map<String,String> properties;
    private String module;
    private String filename;
    private boolean raw;

    public ModulePropertiesRecord() {
        this((Map<String, String>) null, null, null, false);
    }

    public ModulePropertiesRecord(Map<String, String> properties, String module, String filename, boolean raw) {
        this.properties = properties;
        this.module = module;
        this.filename = filename;
        this.raw = raw;
    }

    public ModulePropertiesRecord(Properties props, String module, String filename, boolean raw) {
        this.module = module;
        this.properties = new LinkedHashMap<>();
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            this.properties.put(entry.getKey().toString(), entry.getValue().toString());
        }
        this.filename = filename;
        this.raw = raw;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isRaw() {
        return raw;
    }

    public void setRaw(boolean raw) {
        this.raw = raw;
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, properties, filename, raw);
    }

    @Override
    public String toString() {
        return String.format("ModulePropertiesRecord{module='%s', filename='%s', properties=%s, raw='%s'}",
                module, filename, properties, raw);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ModulePropertiesRecord other = (ModulePropertiesRecord) obj;

        return Objects.equals(this.module, other.module) &&
                Objects.equals(this.filename, other.filename) &&
                Objects.equals(this.properties, other.properties) &&
                Objects.equals(this.raw, other.raw);
    }
}
