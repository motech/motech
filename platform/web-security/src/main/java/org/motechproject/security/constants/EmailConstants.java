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
    public static final String TEMPLATE_PARAM_EXPIRATION_DATE = "expiration_date";
    public static final String TEMPLATE_PARAM_LAST_PASSWORD_CHANGE = "last_password_change";
    public static final String TEMPLATE_PARAM_DAYS_TILL_EXPIRE = "days_till_expire";
    public static final String TEMPLATE_PARAM_SERVER_URL = "server_url";
    public static final String TEMPLATE_PARAM_EXTERNAL_ID = "external_id";

    //E-mail message subjects
    private static final String BASE_MSG = "security.email.subjects.";
    public static final String RECOVERY_MESSAGE_SUBJECT = BASE_MSG + "passwordRecovery";
    public static final String ONE_TIME_TOKEN_MESSAGE_SUBJECT = BASE_MSG + "oneTimeToken";
    public static final String LOGIN_INFORMATION_MESSAGE_SUBJECT = BASE_MSG + "loginInformation";
    public static final String PASSWORD_CHANGE_REMINDER_MESSAGE_SUBJECT = BASE_MSG + "passwordChangeReminder";
    
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
