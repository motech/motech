package org.motechproject.mds.osgi;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'CouchFoo'")
public class CouchFoo extends MotechBaseDataObject {
    private static final long serialVersionUID = -34316443819706508L;

    @JsonProperty
    int couchInt;
    @JsonProperty
    String couchString;

    public CouchFoo() {
        super();
        this.setCouchInt(0);
        this.setCouchString(null);
    }

    public CouchFoo(int couchInt, String couchString) {
        super();
        this.couchInt = couchInt;
        this.couchString = couchString;
    }

    public String getCouchString() {
        return couchString;
    }

    public void setCouchString(String couchString) {
        this.couchString = couchString;
    }

    public int getCouchInt() {
        return couchInt;
    }

    public void setCouchInt(int couchInt) {
        this.couchInt = couchInt;
    }

    @Override
    public String toString() {
        return "CouchFoo{" +
                "couchInt=" + couchInt +
                ", couchString='" + couchString + '\'' +
                '}';
    }
}