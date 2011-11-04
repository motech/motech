package org.motechproject.mobileforms.api.parser;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FormDataParserTest {

    private FormDataParser parser;

    @Before
    public void setUp(){
      parser = new FormDataParser();
    }

    @Test
    public void shouldConvertFormXMLToDataString(){
         String xml = "<?xml version='1.0' encoding='UTF-8' ?>" +
                 "<patientreg description-template=\"${/patientreg/lastname}$ in ${/patientreg/continent}$\" id=\"1\" name=\"Patient Registration\" " +
                 "xmlns:xf=\"http://www.w3.org/2002/xforms\" " +
                 "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                 "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                 "<patientid>123</patientid>" +
                 "<title>mrs</title>" +
                 "<firstname>Test</firstname>" +
                 "<lastname>Test</lastname>" +
                 "<sex>female</sex>" +
                 "<birthdate>1990-06-03</birthdate>" +
                 "<weight>40</weight>" +
                 "<height>20</height>" +
                 "<pregnant>false</pregnant>" +
                 "<continent>africa</continent>" +
                 "<country>uganda</country>" +
                 "<district>mbale</district>" +
                 "<formType>data</formType>" +
                 "<formName>patientreg</formName>" +
                 "</patientreg>";

        Map actual = parser.parse(xml).data();
        assertEquals(14,actual.size());
        assertEquals("uganda",actual.get("country"));
        assertEquals("patientreg",actual.get("formName"));
        assertEquals("data", actual.get("formType"));
    }
}
