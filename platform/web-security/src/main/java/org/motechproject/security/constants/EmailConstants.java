package org.motechproject.security.constants;

/**
 * Utility class for storing constants related to e-mails.
 */
public final class EmailConstants {

    //Templates
    public static final String RESET_MAIL_TEMPLATE = "reset_mail.vm";
    public static final String ONE_TIME_TOKEN_TEMPLATE = "one_time_token_mail.vm";
    public static final String PASSWORD_CHANGE_REMINDER_TEMPLATE = "password_change_reminder.vm";
    public static final String LOGIN_INFORMATION_TEMPLATE = "login_info.vm";
    
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

    //E-mail message subjects
    public static final String RECOVERY_MESSAGE_SUBJECT = "Motech Password Recovery";
    public static final String ONE_TIME_TOKEN_MESSAGE_SUBJECT = "Motech One Time Token For Admin User";
    public static final String LOGIN_INFORMATION_MESSAGE_SUBJECT = "Motech Login Information";
    public static final String PASSWORD_CHANGE_REMINDER_MESSAGE_SUBJECT = "Password change reminder";
    
    //Paths
    public static final String RESET_PATH = "reset";
    public static final String ONE_TIME_TOKEN_PATH = "onetimetoken";

    //Event subjects
    public static final String BASE_EMAIL_SUBJECT = "org.motechproject.security.email.passwordRecovery";
    public static final String PASSWORD_EXPIRATION_CHECK_EVENT = BASE_EMAIL_SUBJECT + ".CheckPasswordExpiration";
    public static final String PASSWORD_CHANGE_REMINDER_EVENT = BASE_EMAIL_SUBJECT + ".PasswordChangeReminder";

    /**
     * Utility class, should not be initiated.
     */
    private EmailConstants() {
    }
}
