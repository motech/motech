package org.motechproject.ivr.kookoo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.server.service.ivr.IVRMessage;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({KookooResponseFactory.class})
public class KookooIVRResponseBuilderTest {

    private KookooIVRResponseBuilder builder;
    @Mock
    private IVRMessage messages;

    @Before
    public void setUp() {
        initMocks(this);
        builder = emptyBuilder();
        Mockito.when(messages.getWav(anyString(), anyString())).thenReturn("");
    }

    private KookooIVRResponseBuilder emptyBuilder() {
        return new KookooIVRResponseBuilder().withSid("sid");
    }

    @Test
    public void shouldAddPlayTextOnlyIfItsNotEmpty() {
        when(messages.getText(anyString())).thenReturn("nova");

        String response = builder.withPlayTexts("nova").create(messages);
        assertTrue(response.contains("nova"));

        builder = emptyBuilder();
        response = builder.create(messages);
        assertFalse(response.contains("<playtext>"));
    }

    @Test
    public void shouldAddPlayAudioOnlyIfItsNotEmpty() {
        when(messages.getWav(anyString(), anyString())).thenReturn("nova");
        String response = builder.withPlayAudios("nova").create(messages);
        assertTrue(response.contains("nova"));

        builder = emptyBuilder();
        response = builder.create(messages);
        assertFalse(response.contains("<playaudio>"));
    }

    @Test
    public void shouldAddCollectDTMFWithCharacterLimit() {
        mockStatic(KookooResponseFactory.class);
        String response = emptyBuilder().collectDtmfLength(4).withPlayAudios("foo").create(messages);
        assertTrue(response.contains("l=\"4\""));
    }

    @Test
    public void shouldHangupOnlyOnlyWhenAskedFor() {
        String response = builder.withHangUp().withSid("sid").create(messages);
        assertTrue(response.contains("<hangup/>"));

        response = new KookooIVRResponseBuilder().withSid("12").create(messages);
        assertFalse(response.contains("<hangup/>"));
    }

    @Test
    public void shouldAddMultiplePlayAudios() {
        when(messages.getWav("wav1", "en")).thenReturn("wav1");
        when(messages.getWav("wav2", "en")).thenReturn("wav2");
        String response = builder.withPlayAudios("wav1").withPlayAudios("wav2").withSid("sid").create(messages);
        assertTrue(response, response.contains("<playaudio>wav1</playaudio>"));
        assertTrue(response, response.contains("<playaudio>wav2</playaudio>"));
    }

    @Test
    public void shouldAddMultiplePlayTexts() {
        when(messages.getText("txt1")).thenReturn("txt1");
        when(messages.getText("txt2")).thenReturn("txt2");
        String response = builder.withPlayTexts("txt1").withPlayTexts("txt2").withSid("sid").create(messages);
        assertTrue(response.contains("<playtext>txt1</playtext>"));
        assertTrue(response.contains("<playtext>txt2</playtext>"));
    }

    @Test
    public void transitionsWithoutAudioPromptsShouldImplyNoUserResponse() {
        assertEquals(false, builder.isCollectDtmf());
        builder = emptyBuilder();
        assertEquals(true, builder.withPlayAudios("foo").isCollectDtmf());
    }
}
