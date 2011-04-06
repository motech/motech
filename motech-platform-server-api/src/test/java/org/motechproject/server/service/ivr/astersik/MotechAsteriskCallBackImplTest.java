/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * “AS IS” AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.server.service.ivr.astersik;

import org.asteriskjava.live.AsteriskChannel;
import org.asteriskjava.live.LiveException;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 *  TODO - develop proper tests
 */
public class MotechAsteriskCallBackImplTest {

    private MotechAsteriskCallBackImpl motechAsteriskCallBack;

    private AsteriskChannel asteriskChannel = mock(AsteriskChannel.class);

    @Before
    public void setup() {
        motechAsteriskCallBack = new MotechAsteriskCallBackImpl();
    }

    @Test
    public void testOnDialing() throws Exception {
        motechAsteriskCallBack.onDialing(asteriskChannel);
    }

    @Test
    public void testOnSuccess() throws Exception {
        motechAsteriskCallBack.onSuccess(asteriskChannel);
    }

    @Test
    public void testOnNoAnswer() throws Exception {
        motechAsteriskCallBack.onNoAnswer(asteriskChannel);
    }

    @Test
    public void testOnBusy() throws Exception {
        motechAsteriskCallBack.onBusy(asteriskChannel);
    }

    @Test
    public void testOnFailure() throws Exception {
        motechAsteriskCallBack.onFailure(new LiveException("error") {
        });
    }
}
