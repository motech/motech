package repository.bundle.domain;

import repository.bundle.domain.HelloWorldRecord;

import java.io.Serializable;
import java.util.Objects;

/**
 * The <code>HelloWorldRecordDto</code> models data for Hello World Records.
 */
public class HelloWorldRecordDto implements Serializable {

    private static final long serialVersionUID = 1213138056532029801L;

    private String name;
    private String message;

    public HelloWorldRecordDto() {
    }

    public HelloWorldRecordDto(HelloWorldRecord record) {
        this(record.getName(), record.getMessage());
    }

    public HelloWorldRecordDto(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, message);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final HelloWorldRecordDto other = (HelloWorldRecordDto) obj;

        return Objects.equals(this.name, other.name) && Objects.equals(this.message, other.message);
    }

    @Override
    public String toString() {
        return String.format("HelloWorldRecordDto{name='%s', message='%s'}", name, message);
    }
}