package org.motechproject.ivr.kookoo;

import org.motechproject.ivr.model.CallDirection;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contract for an IVR Context needed for driving an IVR call flow.
 */
public interface IvrContext {
    /**
     * Gets the Http Request
     * @return
     */
    HttpServletRequest httpRequest();

    /**
     * Gets the Kookoo Request
     * @return
     */
    KookooRequest kooKooRequest();

    /**
     * Gets the DTMF input provided by the user
     * @return
     */
    String userInput();

    /**
     * Gets the current position in the decision tree
     * @return
     */
    String currentDecisionTreePath();

    /**
     * Sets the current position in the decision tree
     * @param path
     */
    void currentDecisionTreePath(String path);

    /**
     * Gets the call Id for a call. Typically, this would be the session Id.
     * @return
     */
    String callId();

    /**
     * Sets the call Id for a call.
     * @param callid
     */
    void callId(String callid);

    /**
     * Gets the preferred language for the user.
     * @return
     */
    String preferredLanguage();

    /**
     * Sets the preferred language for the user.
     * @param languageCode
     */
    void preferredLanguage(String languageCode);

    /**
     * Gets the Id of the Call Detail Record for a call.
     * @return
     */
    String callDetailRecordId();

    /**
     * Sets the Id of the Call Detail Record for a call.
     * @param kooKooCallDetailRecordId
     */
    void callDetailRecordId(String kooKooCallDetailRecordId);

    /**
     * Gets the name of the decision tree being served.
     * @return
     */
    String treeName();

    /**
     * Sets the name of the decision tree to be served.
     * @param treeName
     */
    void treeName(String treeName);

    /**
     * Gets the data to be logged.
     * @return
     */
    HashMap<String, String> dataToLog();

    /**
     * Sets the data to be logged.
     * @param map
     */
    void dataToLog(HashMap<String, String> map);

    /**
     * Gets the list of completed trees.
     * @return
     */
    List<String> getListOfCompletedTrees();

    /**
     * Adds a tree to the list of completed trees.
     * @param lastCompletedTreeName
     */
    void addToListOfCompletedTrees(String lastCompletedTreeName);

    /**
     * Gets the external Id.
     * @return
     */
    String externalId();

    /**
     * Gets the IVR Event.
     * @return
     */
    String ivrEvent();

    /**
     * Gets the caller Id. Typically, this is the phone number of the user.
     * @return
     */
    String callerId();

    /**
     * Gets the call direction, viz., Outbound or Inbound.
     * @return
     */
    CallDirection callDirection();

    /**
     * Returns true if the call has been answered.
     * @return
     */
    boolean isAnswered();

    /**
     * Adds a key-value pair to the Call Session.
     * @param key Any String
     * @param value Any serializable object
     * @param <T> Any serializable type
     */
    <T extends Serializable> void addToCallSession(String key, T value);

    /**
     * Gets the value for a key from the Call Session.
     * @param key Any String
     * @param <T> Any serializable type
     * @return Value casted to the specified type
     */
    <T extends Serializable> T getFromCallSession(String key);
}
