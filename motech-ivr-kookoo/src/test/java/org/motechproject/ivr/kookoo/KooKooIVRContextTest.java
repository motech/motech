package org.motechproject.ivr.kookoo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ivr.domain.CallSessionRecord;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
        CallSessionRecord callSessionRecord = new CallSessionRecord("sessionId");
        callSessionRecord.add(KooKooIVRContext.LIST_OF_COMPLETED_TREES, completedTrees);
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(null, request, null, callSessionRecord);

        kooKooIVRContext.addToListOfCompletedTrees("lastTreeName");

        List<String> updatedTreeList = callSessionRecord.<ArrayList<String>>valueFor(KooKooIVRContext
                .LIST_OF_COMPLETED_TREES);
        assertTrue(updatedTreeList.contains("tree1"));
        assertTrue(updatedTreeList.contains("lastTreeName"));
    }

    @Test
    public void shouldAddFirstTreeToListOfCompletedTrees(){
        CallSessionRecord callSessionRecord = new CallSessionRecord("sessionId");

        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(null, request, null, callSessionRecord);
        kooKooIVRContext.addToListOfCompletedTrees("lastTreeName");

        List<String> updatedTreeList = callSessionRecord.<ArrayList<String>>valueFor(KooKooIVRContext
                .LIST_OF_COMPLETED_TREES);
        assertEquals(1, updatedTreeList.size());
        assertTrue(updatedTreeList.contains("lastTreeName"));
    }

    @Test
    public void shouldStoreTreeName_InTheDataBucket(){
        CallSessionRecord callSessionRecord = new CallSessionRecord("sessionId");
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(null, request, null, callSessionRecord);

        kooKooIVRContext.treeName("symptomTree");

        Map<String, String> updatedLogData = callSessionRecord.<HashMap<String, String>>valueFor(KooKooIVRContext.DATA_TO_LOG);
        assertEquals(1, updatedLogData.size());
        assertEquals("symptomTree", updatedLogData.get(CallEventConstants.TREE_NAME));
    }

    @Test
    public void shouldSetSid_WhenInitialized() {
        KookooRequest kookooRequest = new KookooRequest("sid", " cid", "event", "data", "status");
        HttpServletResponse response = mock(HttpServletResponse.class);
        CallSessionRecord callSessionRecord = mock(CallSessionRecord.class);
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(kookooRequest, request, response, callSessionRecord);
        kooKooIVRContext.initialize();
        verify(request).setAttribute(KooKooIVRContext.CALL_ID, "sid");
        verify(callSessionRecord).add(KooKooIVRContext.CALL_ID, "sid");
    }

    @Test
    public void shouldSetCallDetailRecordId_WhenInitialized() {
        KookooRequest kookooRequest = new KookooRequest("sid", " cid", "event", "data", "status");
        kookooRequest.setParameter(KooKooIVRContext.CALL_DETAIL_RECORD_ID, "1234");
        HttpServletResponse response = mock(HttpServletResponse.class);
        CallSessionRecord callSessionRecord = mock(CallSessionRecord.class);
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(kookooRequest, request, response, callSessionRecord);

        kooKooIVRContext.initialize();
        verify(request, times(1)).setAttribute(KooKooIVRContext.CALL_DETAIL_RECORD_ID, "1234");
        verify(callSessionRecord, times(1)).add(KooKooIVRContext.CALL_DETAIL_RECORD_ID, "1234");
    }
}