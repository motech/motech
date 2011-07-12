package org.motechproject.cmslite.api;


public class ResourceNotFoundException extends Exception{

    public ResourceNotFoundException(){
        super("Resource with specified Name and Language does not exist");
    }
}
