package org.motechproject.mds.service;

/**
 * The <code>HistoryService</code> provides methods related with processing historical changes on
 * the given instance of entity.
 */
public interface HistoryService {

    /**
     * Records changes made on the given instance of entity. The first historical data should be
     * equal to data inside the given instance. Two instance of historical data should be
     * connected using appropriate fields (defined in history class definition).
     *
     * @param historyClass a history class definition. This class should contain the same fields
     *                     like a instance class definition and it should have 3 extra fields:
     *                     {@code historyClass.getSimpleName() + "CurrentVersion"},
     *                     {@code historyClass.getSimpleName() + "Previous"} and
     *                     {@code historyClass.getSimpleName() + "Next"}.
     * @param instance     an instance created from the given entity definition.
     */
    void record(Class<?> historyClass, Object instance);

}
