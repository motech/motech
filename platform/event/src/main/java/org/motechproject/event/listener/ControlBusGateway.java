package org.motechproject.event.listener;

/**
 * The <code>ControlBusGateway</code> interface provides methods to communicate with Control Bus
 */
public interface ControlBusGateway {

    /**
     * Sends command to Control Bus
     *
     * @param command sent to Control Bus
     */
    void sendCommand(String command);

    /**
     * Sends command to Control Bus and receives <code>boolean</code> response
     *
     * @param command sent to Control Bus
     * @return response from Conrol Bus
     */
    boolean sendCommandWithBooleanResponse(String command);
}
