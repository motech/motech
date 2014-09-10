package org.motechproject.config.domain;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static org.apache.commons.io.FilenameUtils.isExtension;

/**
 * The <code>ModulePropertiesRecord</code> class represents a record of a certain
 * module properties.
 *
 * This class is exposed as an {@link org.motechproject.mds.annotations.Entity} through
 * Motech Data Services.
 *
 * @see org.motechproject.mds.annotations
 */
@Entity
public class ModulePropertiesRecord {

    @Ignore
    private static Logger logger = LoggerFactory.getLogger(ModulePropertiesRecord.class);

    @Ignore
    public static final String PROPERTIES_FILE_EXTENSION = "properties";

    @Field
    private Map<String, Object> properties;

    @Field
    private String module;

    @Field
    private String version;

    @Field
    private String bundle;

    @Field
    private String filename;

    @Field
    private boolean raw;

    public ModulePropertiesRecord() {
        this((Map<String, Object>) null, null, null, null, null, false);
    }

    public ModulePropertiesRecord(Map<String, Object> properties, String module, String version, String bundle, String filename, boolean raw) {
        this.properties = properties;
        this.module = module;
        this.version = version;
        this.bundle = bundle;
        this.filename = filename;
        this.raw = raw;
    }

    public ModulePropertiesRecord(Properties props, String module, String version, String bundle, String filename, boolean raw) {
        this.module = module;
        this.properties = new LinkedHashMap<>();
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            this.properties.put(entry.getKey().toString(), entry.getValue());
        }
        this.version = version;
        this.bundle = bundle;
        this.filename = filename;
        this.raw = raw;
    }

    public static ModulePropertiesRecord buildFrom(File file) {
        InputStream inputStream = null;
        try {
            inputStream = FileUtils.openInputStream(file);
            final String fileName = file.getName();
            boolean raw = !isExtension(fileName, PROPERTIES_FILE_EXTENSION);
            Properties properties = buildProperties(inputStream, raw);
            String module = raw ? file.getParentFile().getParentFile().getName() : file.getParentFile().getName();
            return new ModulePropertiesRecord(properties, module, "", "", fileName, raw);
        } catch (IOException e) {
            logger.error(String.format("Error reading config file %s", file.getAbsolutePath()), e);
            return null;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private static Properties buildProperties(InputStream inputStream, boolean raw) throws IOException {
        Properties properties = new Properties();
        if (raw) {
            properties.put("rawData", IOUtils.toString(inputStream));
        } else {
            properties.load(inputStream);
        }
        return properties;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public boolean sameAs(Object dataObject) {
        ModulePropertiesRecord record = (ModulePropertiesRecord) dataObject;
        return new EqualsBuilder()
                .append(this.module, record.module)
                .append(this.version, record.version)
                .append(this.bundle, record.bundle)
                .append(this.filename, record.filename)
                .append(this.raw, record.raw)
                .isEquals();
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
                Objects.equals(this.version, other.version) &&
                Objects.equals(this.bundle, other.bundle) &&
                Objects.equals(this.properties, other.properties) &&
                Objects.equals(this.raw, other.raw);
    }
}
