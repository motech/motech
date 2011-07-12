package org.motechproject.cmslite;


public class ResourceNotFoundException extends Exception{

    public ResourceNotFoundException(){
        super("Resource with specified Name and Language does not exist");
    }
}
