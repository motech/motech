package org.motechproject.config.domain;

/**
 * Encapsulates the operations on login mode.
 */
public final class LoginMode {


    public static final LoginMode REPOSITORY = new LoginMode("repository");
    public static final LoginMode OPEN_ID = new LoginMode("openId");

    private String loginMode;

    private LoginMode(String loginMode) {
        this.loginMode = loginMode;
    }

    /**
     * Checks if this login mode is set to "Repository".
     *
     * @return true if this login mode is set to "Repository", false otherwise
     */
    public boolean isRepository() {
        return isEqual(REPOSITORY.getName());
    }

    /**
     * Checks if this login mode is set to "Open ID".
     *
     * @return true if this login mode is set to "Open ID", false otherwise
     */
    public boolean isOpenId() {
        return isEqual(OPEN_ID.getName());
    }

    /**
     * Checks if given authentication mode is the same as this login mode.
     * @param authenticationMode  the authentication mode
     * @return true if both objects are equal, false otherwise
     */
    private boolean isEqual(String authenticationMode) {
        return authenticationMode.equalsIgnoreCase(loginMode);
    }

    public String getName() {
        return loginMode;
    }

    /**
     * Creates proper login mode from given {@code String}, which can be either "repository" or "openId".
     *
     * @param loginMode  the login mode to be created, must be either "repository" or "openId", other values will return null
     * @return the proper object of {@code LoginMode}, null if given value was neither "repository" nor "openId"
     */
    public static LoginMode valueOf(String loginMode) {
        return REPOSITORY.loginMode.equalsIgnoreCase(loginMode) ? REPOSITORY :
               OPEN_ID.loginMode.equalsIgnoreCase(loginMode) ? OPEN_ID : null;
    }
}
