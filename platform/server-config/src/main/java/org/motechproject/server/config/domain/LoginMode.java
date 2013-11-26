package org.motechproject.server.config.domain;

/**
 * Encapsulates the operations on login mode
 */
public final class LoginMode {


    public static final LoginMode REPOSITORY = new LoginMode("repository");
    public static final LoginMode OPEN_ID = new LoginMode("openId");

    private String loginMode;

    private LoginMode(String loginMode) {
        this.loginMode = loginMode;
    }

    public boolean isRepository() {
        return isEqual(REPOSITORY.getName());
    }

    public boolean isOpenId() {
        return isEqual(OPEN_ID.getName());
    }

    private boolean isEqual(String authenticationMode) {
        return authenticationMode.equalsIgnoreCase(loginMode);
    }

    public String getName() {
        return loginMode;
    }

    public static LoginMode valueOf(String loginMode) {
        return REPOSITORY.loginMode.equalsIgnoreCase(loginMode) ? REPOSITORY :
               OPEN_ID.loginMode.equalsIgnoreCase(loginMode) ? OPEN_ID : null;
    }
}
