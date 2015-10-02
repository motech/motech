package org.motechproject.security.constants;

/**
 * Utility class for storing constants related to e-mails.
 */
public final class EmailConstants {

    //Templates
    public static final String RESET_MAIL_TEMPLATE = "resetMail.vm";
    public static final String ONE_TIME_TOKEN_TEMPLATE = "oneTimeTokenMail.vm";
    public static final String PASSWORD_CHANGE_REMINDER_TEMPLATE = "passwordChangeReminder.vm";
    public static final String LOGIN_INFORMATION_TEMPLATE = "loginInfo.vm";
    
    //E-mail parameters
    public static final String EMAIL_PARAM_FROM_ADDRESS = "fromAddress";
    public static final String EMAIL_PARAM_TO_ADDRESS = "toAddress";
    public static final String EMAIL_PARAM_MESSAGE = "message";
    public static final String EMAIL_PARAM_SUBJECT = "subject";
    
    //Template parameters
    public static final String TEMPLATE_PARAM_LINK = "link";
    public static final String TEMPLATE_PARAM_USERNAME = "user";
    public static final String TEMPLATE_PARAM_MESSAGES = "messages";
    public static final String TEMPLATE_PARAM_LOCALE = "locale";
    public static final String TEMPLATE_PARAM_EXPIRATION_DATE = "expirationDate";
    public static final String TEMPLATE_PARAM_LAST_PASSWORD_CHANGE = "lastPasswordChange";
    public static final String TEMPLATE_PARAM_DAYS_TILL_EXPIRE = "daysTillExpire";
    public static final String TEMPLATE_PARAM_SERVER_URL = "serverUrl";
    public static final String TEMPLATE_PARAM_EXTERNAL_ID = "externalId";

    //E-mail subjects
    public static final String RECOVERY_SUBJECT = "Motech Password Recovery";
    public static final String ONE_TIME_TOKEN_SUBJECT = "Motech One Time Token For Admin User";
    public static final String LOGIN_INFORMATION_SUBJECT = "Motech Login Information";
    public static final String PASSWORD_CHANGE_REMINDER_SUBJECT = "Password change reminder";
    
    //Paths
    public static final String RESET_PATH = "reset";
    public static final String ONE_TIME_TOKEN_PATH = "onetimetoken";
    
    /**
     * Utility class, should not be initiated.
     */
    private EmailConstants() {
    }
}
