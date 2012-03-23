package org.motechproject.ivr.kookoo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
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
    @Mock
    private HttpSession session;


    @Before
    public void setUp() {
        initMocks(this);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void shouldAddTreeToListOfCompletedTrees(){
        List<String> completedTrees = new ArrayList<String>() {{
            this.add("tree1");
        }};
        when(session.getAttribute(KooKooIVRContext.LIST_OF_COMPLETED_TREES)).thenReturn(completedTrees);
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(null, request, null);
        kooKooIVRContext.addToListOfCompletedTrees("lastTreeName");
        verify(session).setAttribute(KooKooIVRContext.LIST_OF_COMPLETED_TREES, completedTrees);
        assertTrue(completedTrees.contains("tree1"));
        assertTrue(completedTrees.contains("lastTreeName"));
    }

    @Test
    public void shouldAddFirstTreeToListOfCompletedTrees(){
        when(session.getAttribute(KooKooIVRContext.LIST_OF_COMPLETED_TREES)).thenReturn(null);
        ArgumentCaptor<ArrayList> valueCaptor = ArgumentCaptor.forClass(ArrayList.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);

        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(null, request, null);
        kooKooIVRContext.addToListOfCompletedTrees("lastTreeName");

        verify(session).setAttribute(keyCaptor.capture(), valueCaptor.capture());
        assertEquals(KooKooIVRContext.LIST_OF_COMPLETED_TREES, keyCaptor.getAllValues().get(0));
        assertEquals(1, valueCaptor.getValue().size());
        assertTrue(valueCaptor.getValue().contains("lastTreeName"));
    }

    @Test
    public void shouldStoreTreeName_InTheDataBucket(){
        when(session.getAttribute(KooKooIVRContext.DATA_TO_LOG)).thenReturn(null);
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(null, request, null);

        kooKooIVRContext.treeName("symptomTree");

        ArgumentCaptor<Map> dataBucketMapCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);

        verify(session).setAttribute(keyCaptor.capture(), dataBucketMapCaptor.capture());
        assertEquals(KooKooIVRContext.DATA_TO_LOG, keyCaptor.getValue());
        assertEquals(1, dataBucketMapCaptor.getValue().size());
        assertEquals("symptomTree", dataBucketMapCaptor.getValue().get(CallEventConstants.TREE_NAME));
    }

    @Test
    public void shouldSetSid_WhenInitialized() {
        KookooRequest kookooRequest = new KookooRequest("sid", " cid", "event", "data", "status");
        HttpServletResponse response = mock(HttpServletResponse.class);
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(kookooRequest, request, response);
        kooKooIVRContext.initialize();
        verify(request).setAttribute(KooKooIVRContext.CALL_ID, "sid");
        ArgumentCaptor<Cookie> cookieArgumentCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(2)).addCookie(cookieArgumentCaptor.capture());
        assertEquals(KooKooIVRContext.CALL_ID, cookieArgumentCaptor.getAllValues().get(0).getName());
        assertEquals("sid", cookieArgumentCaptor.getAllValues().get(0).getValue());
    }

    @Test
    public void shouldSetCallDetailRecordId_WhenInitialized() {
        KookooRequest kookooRequest = new KookooRequest("sid", " cid", "event", "data", "status");
        kookooRequest.setParameter(KooKooIVRContext.CALL_DETAIL_RECORD_ID, "1234");
        HttpServletResponse response = mock(HttpServletResponse.class);
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(kookooRequest, request, response);

        kooKooIVRContext.initialize();
        verify(request, times(1)).setAttribute(KooKooIVRContext.CALL_DETAIL_RECORD_ID, "1234");
        ArgumentCaptor<Cookie> cookieArgumentCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(2)).addCookie(cookieArgumentCaptor.capture());
        assertEquals(KooKooIVRContext.CALL_DETAIL_RECORD_ID, cookieArgumentCaptor.getAllValues().get(1).getName());
        assertEquals("1234", cookieArgumentCaptor.getAllValues().get(1).getValue());
    }
}