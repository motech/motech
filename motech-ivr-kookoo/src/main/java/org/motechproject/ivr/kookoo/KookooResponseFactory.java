package org.motechproject.ivr.kookoo;

public final class KookooResponseFactory {
    private KookooResponseFactory() { }

    public static KookooIVRResponseBuilder hangUpResponseWith(String callId) {
        return new KookooIVRResponseBuilder().withHangUp().withSid(callId);
    }

    public static KookooIVRResponseBuilder dtmfResponseWithWav(String callId, String wavFile) {
        return new KookooIVRResponseBuilder().withPlayAudios(wavFile).withSid(callId);
    }

    public static KookooIVRResponseBuilder empty(String callId) {
        return new KookooIVRResponseBuilder().withSid(callId);
    }
}
