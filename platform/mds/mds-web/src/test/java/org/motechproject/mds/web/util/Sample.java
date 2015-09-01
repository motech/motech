package org.motechproject.mds.web.util;


import org.motechproject.mds.annotations.UIRepresentation;

public class Sample {

    @UIRepresentation
    public String uiRepresentation(){
        return "testString";
    }
}

class Sample1 {

    @UIRepresentation
    public String uiRepresentation1() {
        return "testString1";
    }

    @UIRepresentation
    public String uiRepresentation2() {
        return "testString2";
    }
}

class Sample2 {

    @UIRepresentation
    public String uiReresentation() {
        return "testString";
    }

    @UIRepresentation
    public Integer invalidUIRepresentation() {
        return 0;
    }
}
