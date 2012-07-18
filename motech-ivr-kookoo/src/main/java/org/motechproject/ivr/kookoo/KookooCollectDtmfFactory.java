package org.motechproject.ivr.kookoo;

import com.ozonetel.kookoo.CollectDtmf;

public final class KookooCollectDtmfFactory {
    private KookooCollectDtmfFactory() { }

    public static CollectDtmf create() {
        return new CollectDtmf();
    }
}
