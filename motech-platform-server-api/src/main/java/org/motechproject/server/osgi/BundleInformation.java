/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2012 Grameen Foundation USA.  All rights reserved.
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
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
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
package org.motechproject.server.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

public class BundleInformation {
    private enum State {        
        UNINSTALLED(1),
        INSTALLED(2),
        RESOLVED(4),
        STARTING(8),
        STOPPING(16),
        ACTIVE(32),
        UNKNOWN(0);

        int stateId;

        State(int stateId) {
            this.stateId = stateId;
        }
        
        public static State fromInt(int stateId) {
            for (State state : values()) {
                if (stateId == state.stateId) {
                    return state;
                }
            }
            return UNKNOWN;
        }
    }
    
    private long bundleId;
    private Version version;
    private String symbolicName;
    private String location;
    private State state;

    public BundleInformation(Bundle bundle) {
        this.bundleId = bundle.getBundleId();
        this.version = bundle.getVersion();
        this.symbolicName = bundle.getSymbolicName();
        this.location = bundle.getLocation();
        this.state = State.fromInt(bundle.getState());
    }

    public long getBundleId() {
        return bundleId;
    }

    public Version getVersion() {
        return version;
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    public String getLocation() {
        return location;
    }

    public State getState() {
        return state;
    }
}
