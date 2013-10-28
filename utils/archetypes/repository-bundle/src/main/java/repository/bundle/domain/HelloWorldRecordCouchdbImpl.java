package repository.bundle.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

/**
 * CouchDb implementation of {@link HelloWorldRecord}.
 */
@TypeDiscriminator("doc.type == 'HelloWorldRecord'")
public class HelloWorldRecordCouchdbImpl extends MotechBaseDataObject implements HelloWorldRecord {

    private static final long serialVersionUID = -2808972968196521169L;
    
    public static final String DOC_TYPE = "HelloWorldRecord";

    @JsonProperty
    private String name;

    @JsonProperty
    private String message;

    public HelloWorldRecordCouchdbImpl() {
        super();
        this.setType(DOC_TYPE);
    }

    public HelloWorldRecordCouchdbImpl(String name, String message) {
        super();
        this.name = name;
        this.message = message;
        this.setType(DOC_TYPE);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
