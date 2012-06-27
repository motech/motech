package org.motechproject.ivr.kookoo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.domain.FlowSessionRecord;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class KooKooIVRContextTest {
    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldAddTreeToListOfCompletedTrees(){
        ArrayList<String> completedTrees = new ArrayList<String>() {{
            this.add("tree1");
        }};
        FlowSessionRecord flowSessionRecord = new FlowSessionRecord("sessionId");
        flowSessionRecord.set(KooKooIVRContext.LIST_OF_COMPLETED_TREES, completedTrees);
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(null, request, null, flowSessionRecord);

        kooKooIVRContext.addToListOfCompletedTrees("lastTreeName");

        List<String> updatedTreeList = flowSessionRecord.<ArrayList<String>>get(KooKooIVRContext
                .LIST_OF_COMPLETED_TREES);
        assertTrue(updatedTreeList.contains("tree1"));
        assertTrue(updatedTreeList.contains("lastTreeName"));
    }

    @Test
    public void shouldAddFirstTreeToListOfCompletedTrees(){
        FlowSessionRecord flowSessionRecord = new FlowSessionRecord("sessionId");

        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(null, request, null, flowSessionRecord);
        kooKooIVRContext.addToListOfCompletedTrees("lastTreeName");

        List<String> updatedTreeList = flowSessionRecord.<ArrayList<String>>get(KooKooIVRContext
                .LIST_OF_COMPLETED_TREES);
        assertEquals(1, updatedTreeList.size());
        assertTrue(updatedTreeList.contains("lastTreeName"));
    }

    @Test
    public void shouldStoreTreeName_InTheDataBucket(){
        FlowSessionRecord flowSessionRecord = new FlowSessionRecord("sessionId");
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(null, request, null, flowSessionRecord);

        kooKooIVRContext.treeName("symptomTree");

        Map<String, String> updatedLogData = flowSessionRecord.<HashMap<String, String>>get(KooKooIVRContext.DATA_TO_LOG);
        assertEquals(1, updatedLogData.size());
        assertEquals("symptomTree", updatedLogData.get(CallEventConstants.TREE_NAME));
    }

    @Test
    public void shouldSetSid_WhenInitialized() {
        KookooRequest kookooRequest = new KookooRequest("sid", " cid", "event", "data", "status");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FlowSessionRecord flowSessionRecord = mock(FlowSessionRecord.class);
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(kookooRequest, request, response, flowSessionRecord);
        kooKooIVRContext.initialize();
        verify(request).setAttribute(KooKooIVRContext.CALL_ID, "sid");
        verify(flowSessionRecord).set(KooKooIVRContext.CALL_ID, "sid");
    }

    @Test
    public void shouldSetCallDetailRecordId_WhenInitialized() {
        KookooRequest kookooRequest = new KookooRequest("sid", " cid", "event", "data", "status");
        kookooRequest.setParameter(KooKooIVRContext.CALL_DETAIL_RECORD_ID, "1234");
        HttpServletResponse response = mock(HttpServletResponse.class);
        FlowSessionRecord flowSessionRecord = mock(FlowSessionRecord.class);
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(kookooRequest, request, response, flowSessionRecord);

        kooKooIVRContext.initialize();
        verify(request, times(1)).setAttribute(KooKooIVRContext.CALL_DETAIL_RECORD_ID, "1234");
        verify(flowSessionRecord, times(1)).set(KooKooIVRContext.CALL_DETAIL_RECORD_ID, "1234");
    }
}