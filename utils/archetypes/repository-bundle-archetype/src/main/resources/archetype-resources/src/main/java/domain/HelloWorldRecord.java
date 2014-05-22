#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.io.Serializable;
import java.util.Objects;

/**
 * Models data for simple records in a portable manner.
 */
@Entity
public class HelloWorldRecord {

    @Field
    private String name;

    @Field
    private String message;

    public HelloWorldRecord() {
    }


    public HelloWorldRecord(String name, String message) {
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

        final HelloWorldRecord other = (HelloWorldRecord) obj;

        return Objects.equals(this.name, other.name) && Objects.equals(this.message, other.message);
    }

    @Override
    public String toString() {
        return String.format("HelloWorldRecord{name='%s', message='%s'}", name, message);
    }
}