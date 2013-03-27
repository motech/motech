package org.motechproject.cmslite.api.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

import java.io.InputStream;
import java.util.Objects;

/**
 * \ingroup cmslite
 * Represents stream content along with checksum.
 */
@TypeDiscriminator("doc.type === 'StreamContent'")
public class StreamContent extends Content {
    private static final long serialVersionUID = 8169367710567919494L;

    private InputStream inputStream;
    @JsonProperty
    private String checksum;
    @JsonProperty
    private String contentType;

    public StreamContent() {
        this(null, null, null, null, null);
    }

    public StreamContent(String language, String name, InputStream inputStream, String checksum, String contentType) {
        super(language, name);
        this.inputStream = inputStream;
        this.checksum = checksum;
        this.contentType = contentType;
    }

    @JsonIgnore
    public InputStream getInputStream() {
        return inputStream;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getContentType() {
        return contentType;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputStream, checksum, contentType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        final StreamContent other = (StreamContent) obj;

        return Objects.equals(this.inputStream, other.inputStream) &&
                Objects.equals(this.checksum, other.checksum) &&
                Objects.equals(this.contentType, other.contentType);
    }

    @Override
    public String toString() {
        return String.format("StreamContent{inputStream=%s, checksum='%s', contentType='%s'} %s",
                inputStream, checksum, contentType, super.toString());
    }
}
