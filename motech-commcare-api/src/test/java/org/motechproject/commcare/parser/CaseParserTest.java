package org.motechproject.commcare.parser;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import org.motechproject.commcare.domain.CaseXml;
import org.motechproject.commcare.exception.CaseParserException;
import org.motechproject.commcare.parser.CaseParser;

import java.io.FileNotFoundException;

public class CaseParserTest extends TestCase {

    @Test
    public void testShouldParseCaseAttributesCorrectly()
            throws FileNotFoundException, CaseParserException {
        CaseParser<CaseXml> parser = new CaseParser<CaseXml>(CaseXml.class,
                caseXml());
        CaseXml aCase = parser.parseCase();
        Assert.assertEquals("3F2504E04F8911D39A0C0305E82C3301",
                aCase.getCase_id());
        Assert.assertEquals("2011-12-08T13:34:30", aCase.getDate_modified());
        Assert.assertEquals("F0183EDA012765103CB106821BBA51A0",
                aCase.getUser_id());
        Assert.assertEquals("2Z2504E04F8911D39A0C0305E82C3000",
                aCase.getOwner_id());
    }

    @Test
    public void testShouldParseCreateAttributesCorrectly()
            throws FileNotFoundException, CaseParserException {
        CaseParser<CaseXml> parser = new CaseParser<CaseXml>(CaseXml.class,
                caseXml());
        CaseXml aCase = parser.parseCase();
        Assert.assertEquals("houshold_rollout_ONICAF", aCase.getCase_type());
        Assert.assertEquals("Smith", aCase.getCase_name());
    }

    @Test
    public void testShouldFetchAPIKeyFromCaseXML()
            throws FileNotFoundException, CaseParserException {
        CaseParser<CaseXml> parser = new CaseParser<CaseXml>(CaseXml.class,
                caseXml());
        CaseXml aCase = parser.parseCase();
        Assert.assertEquals("API_KEY", aCase.getApi_key());
    }

    @Test
    public void testShouldParseCloseAttributesCorrectly()
            throws FileNotFoundException, CaseParserException {
        CaseParser<CaseXml> parser = new CaseParser<CaseXml>(CaseXml.class,
                caseXmlForClose());
        CaseXml aCase = parser.parseCase();
        assertEquals("CLOSE", parser.getCaseAction());
        assertEquals("3F2504E04F8911D39A0C0305E82C3301", aCase.getCase_id());
    }

    private String caseXmlForClose() {
        return "<?xml version=\"1.0\"?>"
                + "<case xmlns=\"http://commcarehq.org/case/transaction/v2\" case_id=\"3F2504E04F8911D39A0C0305E82C3301\" date_modified=\"2012-04-03\" user_id=\"F0183EDA012765103CB106821BBA51A0\">"
                + "    <close />" + "</case>";
    }

    @Test
    public void testShouldSetActionCorrectly() throws FileNotFoundException,
            CaseParserException {
        CaseParser<CaseXml> parser = new CaseParser<CaseXml>(CaseXml.class,
                caseXml());
        CaseXml aCase = parser.parseCase();
        Assert.assertEquals("CREATE", aCase.getAction());
    }

    @Test
    public void testShouldParseIndexElementCorrectly()
            throws FileNotFoundException, CaseParserException {
        CaseParser<CaseXml> parser = new CaseParser<CaseXml>(CaseXml.class,
                childXml());
        CaseXml aCase = parser.parseCase();
        Assert.assertEquals("45134cf7-90f8-4284-8ca1-16392fc0ce57",
                aCase.getCase_id());
        Assert.assertEquals("2012-04-03", aCase.getDate_modified());
        Assert.assertEquals("d823ea3d392a06f8b991e9e4933348bd",
                aCase.getUser_id());
        Assert.assertEquals("d823ea3d392a06f8b991e9e49394ce45",
                aCase.getOwner_id());
        Assert.assertEquals("motherCaseId",
                aCase.getFieldValues().get("mother_id"));
    }

    private String caseXml() {
        String caseXml = "<case xmlns=\"http://commcarehq.org/case/transaction/v2\" case_id=\"3F2504E04F8911D39A0C0305E82C3301\" user_id=\"F0183EDA012765103CB106821BBA51A0\" date_modified=\"2011-12-08T13:34:30\" api_key=\"API_KEY\" >\n"
                + "<create>"
                + "<case_type>houshold_rollout_ONICAF</case_type>"
                + "<case_name>Smith</case_name>"
                + "<owner_id>2Z2504E04F8911D39A0C0305E82C3000</owner_id>"
                + "</create>"
                + "<update>"
                + "<household_id>24/F23/3</household_id>"
                + "<primary_contact_name>Tom Smith</primary_contact_name>"
                + "<visit_number>1</visit_number>" + "</update>" + "</case>";

        return caseXml;
    }

    private String childXml() {
        String caseXml = "<case xmlns=\"http://commcarehq.org/case/transaction/v2\" case_id=\"45134cf7-90f8-4284-8ca1-16392fc0ce57\" date_modified=\"2012-04-03\" user_id=\"d823ea3d392a06f8b991e9e4933348bd\">\n"
                + "<create>"
                + "<case_type>cc_bihar_newborn</case_type>"
                + "<case_name>RAM</case_name>"
                + "<owner_id>d823ea3d392a06f8b991e9e49394ce45</owner_id>"
                + "</create>"
                + "<update>"
                + "<gender>male</gender>"
                + "</update>"
                + "<index>"
                + "<mother_id case_type=\"cc_bihar_pregnancy\">motherCaseId</mother_id>"
                + "</index>" + "</case>";

        return caseXml;
    }
}
