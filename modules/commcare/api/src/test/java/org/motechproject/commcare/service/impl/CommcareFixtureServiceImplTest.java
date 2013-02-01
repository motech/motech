package org.motechproject.commcare.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commcare.domain.CommcareFixture;
import org.motechproject.commcare.domain.CommcareUser;
import org.motechproject.commcare.util.CommCareAPIHttpClient;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommcareFixtureServiceImplTest {

    private CommcareFixtureServiceImpl fixtureService;

    @Mock
    private CommCareAPIHttpClient commcareHttpClient;

    @Before
    public void setUp() {
        initMocks(this);
        fixtureService = new CommcareFixtureServiceImpl(commcareHttpClient);
    }

    @Test
    public void testAllFixtures() {
        when(commcareHttpClient.fixturesRequest()).thenReturn(fixturesResponse());

        List<CommcareFixture> fixtures = fixtureService.getAllFixtures();

        assertEquals(fixtures.size(), 30);
    }

    @Test
    public void testGetFixtureWhenFixtureExists() {
        String fixtureId = "753e8f42bf7d88965be84d3ace555d77";

        when(commcareHttpClient.fixtureRequest(fixtureId)).thenReturn(fixtureResponse());

        CommcareFixture fixture = fixtureService.getCommcareFixtureById(fixtureId);

        assertNotNull(fixture);
        assertEquals(fixture.getId(), fixtureId);
        assertEquals(fixture.getFields().get("phu_id"), "komende");
    }

    @Test
    public void testGetUserWhenUserDoesNotExist() {
        String fixtureId = "badId";

        when(commcareHttpClient.fixtureRequest(fixtureId)).thenReturn("");

        CommcareFixture fixture = fixtureService.getCommcareFixtureById(fixtureId);

        assertNull(fixture);
    }

    private String fixturesResponse() {
        return "{\"meta\": {\"limit\": 0, \"offset\": 0, \"total_count\": 30}, \"objects\": [{\"fields\": {\"adp_id\": \"imperi\", \"adp_name\": \"Imperi\"}, \"fixture_type\": \"adp\", \"id\": \"5ccad6b00684ec354e1e759513b15223\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"kks\", \"adp_name\": \"KKS\"}, \"fixture_type\": \"adp\", \"id\": \"5ccad6b00684ec354e1e759513b153ad\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"jong\", \"adp_name\": \"Jong\"}, \"fixture_type\": \"adp\", \"id\": \"5ccad6b00684ec354e1e759513b16002\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"sherbro\", \"adp_name\": \"Sherbro\"}, \"fixture_type\": \"adp\", \"id\": \"5ccad6b00684ec354e1e759513dae645\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"kks\", \"phu_id\": \"motuo\", \"phu_name\": \"Motuo\"}, \"fixture_type\": \"phu\", \"id\": \"0710bdc649807d93577e25441e9da6cb\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"kks\", \"phu_id\": \"mandu\", \"phu_name\": \"Mandu\"}, \"fixture_type\": \"phu\", \"id\": \"225831ccffdd1450a7de31f7590fe1c1\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"imperi\", \"phu_id\": \"gbangbama\", \"phu_name\": \"Gbangbama\"}, \"fixture_type\": \"phu\", \"id\": \"32bf0d94de8284095f99b4246813d001\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"kks\", \"phu_id\": \"senjehun\", \"phu_name\": \"Senjehun\"}, \"fixture_type\": \"phu\", \"id\": \"5ccad6b00684ec354e1e759513b120a5\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"kks\", \"phu_id\": \"lawana\", \"phu_name\": \"Lawana\"}, \"fixture_type\": \"phu\", \"id\": \"5ccad6b00684ec354e1e759513b12ba7\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"jong\", \"phu_id\": \"gambia\", \"phu_name\": \"Gambia\"}, \"fixture_type\": \"phu\", \"id\": \"5ccad6b00684ec354e1e759513b13a6b\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"jong\", \"phu_id\": \"mattru\", \"phu_name\": \"Mattru\"}, \"fixture_type\": \"phu\", \"id\": \"5ccad6b00684ec354e1e759513b13da9\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"jong\", \"phu_id\": \"mongerewa\", \"phu_name\": \"Mongerewa\"}, \"fixture_type\": \"phu\", \"id\": \"5ccad6b00684ec354e1e759513b140d6\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"jong\", \"phu_id\": \"slrc\", \"phu_name\": \"SLRC\"}, \"fixture_type\": \"phu\", \"id\": \"5ccad6b00684ec354e1e759513b146f5\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"kks\", \"phu_id\": \"kanga\", \"phu_name\": \"Kanga\"}, \"fixture_type\": \"phu\", \"id\": \"5ccad6b00684ec354e1e759513dadb53\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"kks\", \"phu_id\": \"gbongeh \", \"phu_name\": \"Gbongeh \"}, \"fixture_type\": \"phu\", \"id\": \"6ad312ceb2ad36cc79b7579155988eea\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"jong\", \"phu_id\": \"moyowa\", \"phu_name\": \"Moyowa\"}, \"fixture_type\": \"phu\", \"id\": \"6ad312ceb2ad36cc79b7579155ded4b1\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"kks\", \"phu_id\": \"ngueh\", \"phu_name\": \"Ngueh\"}, \"fixture_type\": \"phu\", \"id\": \"753e8f42bf7d88965be84d3ace2b68a7\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"kks\", \"phu_id\": \"tihun\", \"phu_name\": \"Tihun\"}, \"fixture_type\": \"phu\", \"id\": \"753e8f42bf7d88965be84d3ace2b7116\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"jong\", \"phu_id\": \"kabati\", \"phu_name\": \"Kabati\"}, \"fixture_type\": \"phu\", \"id\": \"753e8f42bf7d88965be84d3ace2b7261\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"jong\", \"phu_id\": \"jorma\", \"phu_name\": \"Jorma\"}, \"fixture_type\": \"phu\", \"id\": \"753e8f42bf7d88965be84d3ace555238\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"jong\", \"phu_id\": \"komende\", \"phu_name\": \"Komende\"}, \"fixture_type\": \"phu\", \"id\": \"753e8f42bf7d88965be84d3ace555d77\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"jong\", \"phu_id\": \"gbaninga\", \"phu_name\": \"Gbaninga\"}, \"fixture_type\": \"phu\", \"id\": \"753e8f42bf7d88965be84d3ace791a07\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"jong\", \"phu_id\": \"semabu\", \"phu_name\": \"Semabu\"}, \"fixture_type\": \"phu\", \"id\": \"753e8f42bf7d88965be84d3acec7d83e\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"imperi\", \"phu_id\": \"moribatown \", \"phu_name\": \"Moriba town \"}, \"fixture_type\": \"phu\", \"id\": \"7fe46dee8f20f9518ee8fa6a8ec312fe\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"imperi\", \"phu_id\": \"jangalor\", \"phu_name\": \"Jangalor\"}, \"fixture_type\": \"phu\", \"id\": \"7fe46dee8f20f9518ee8fa6a8ec3180d\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"imperi\", \"phu_id\": \"gbangbaia\", \"phu_name\": \"Gbangbaia\"}, \"fixture_type\": \"phu\", \"id\": \"7fe46dee8f20f9518ee8fa6a8ec31949\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"imperi\", \"phu_id\": \"mogbwemo\", \"phu_name\": \"Mogbwemo\"}, \"fixture_type\": \"phu\", \"id\": \"7fe46dee8f20f9518ee8fa6a8eed76dd\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"imperi\", \"phu_id\": \"juntionla\", \"phu_name\": \"Juntionla\"}, \"fixture_type\": \"phu\", \"id\": \"7fe46dee8f20f9518ee8fa6a8eed8420\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"imperi\", \"phu_id\": \"yargoi\", \"phu_name\": \"Yargoi\"}, \"fixture_type\": \"phu\", \"id\": \"f5078c110d0da8ed22eeb31920187db2\", \"resource_uri\": \"\"}, {\"fields\": {\"adp_id\": \"imperi\", \"phu_id\": \"victoria\", \"phu_name\": \"Victoria\"}, \"fixture_type\": \"phu\", \"id\": \"f5078c110d0da8ed22eeb319203f7db4\", \"resource_uri\": \"\"}]}";
    }

    private String fixtureResponse() {
        return "{\"fields\": {\"adp_id\": \"jong\", \"phu_id\": \"komende\", \"phu_name\": \"Komende\"}, \"fixture_type\": \"phu\", \"id\": \"753e8f42bf7d88965be84d3ace555d77\", \"resource_uri\": \"\"}";
    }
}
