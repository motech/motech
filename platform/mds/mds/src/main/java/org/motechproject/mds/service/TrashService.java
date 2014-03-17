package org.motechproject.mds.service;

import org.motechproject.event.MotechEvent;

import java.util.Collection;

/**
 * The <code>TrashService</code> provides methods related with the module trash mode (by default
 * the mode is active and it can be turned off by the user).
 */
public interface TrashService {

    /**
     * Checks if trash mode is active. This method should be used before executing the
     * {@link #moveToTrash(Object, Long)} method to resolve whether the given instance should be moved to
     * trash or removed permanently.
     *
     * @return true if delete mode is equal to
     * {@link org.motechproject.mds.config.DeleteMode#TRASH}; false otherwise.
     */
    boolean isTrashMode();

    /**
     * Moves the given instance to the trash. This method should only be executed, when the
     * module trash mode is active.
     *
     * @param instance an instance created from the given entity definition.
     * @see #isTrashMode()
     */
    void moveToTrash(Object instance, Long schemaVersion);

    /**
     * Return instance with given id from trash.
     *
     * @param instanceId id of instance
     * @param entityId id of instance entity
     */
    Object findTrashById(Object instanceId, Object entityId);

    /**
     * Sets history for given trashed instance to match the new one
     * and deletes trashed one from trash.
     *
     * @param newInstance instance to be returned from trash
     * @param trash trashed instance to be removed
     */
    void moveFromTrash(Object newInstance, Object trash);

    /**
     * Sets the repeating schedule job that will be executed from time to time. Execution time
     * depends on the value of time value and time unit (defined in
     * {@link org.motechproject.mds.util.Constants.Config#MODULE_FILE}).
     * <p/>
     * Before scheduling new job, the old one should be unscheduled to prevent the errors.
     *
     * @param event the instance of the {@link org.motechproject.event.MotechEvent}. It doesn't
     *              contain any parameters. It is required by the event annotation processor.
     * @see org.motechproject.event.listener.annotations.EventAnnotationBeanPostProcessor
     * @see org.motechproject.event.listener.EventRelay
     */
    void scheduleEmptyTrashEvent(MotechEvent event);

    /**
     * Cleans the module trash. All instances in trash should be removed permanently and if they
     * contain any historical data they should also be removed permanently.
     * <p/>
     * This method should only be executed by the job created in the
     * {@link #scheduleEmptyTrashEvent(org.motechproject.event.MotechEvent)} method.
     *
     * @param event the instance of the {@link org.motechproject.event.MotechEvent}. it contains
     *              only the job id key. It is required by the event annotation processor.
     * @see org.motechproject.event.listener.annotations.EventAnnotationBeanPostProcessor
     * @see org.motechproject.event.listener.EventRelay
     */
    void emptyTrash(MotechEvent event);

    /**
     * Returns the collection of instances from trash of a certain entity.
     * Returned collection contains only instances that are on the current schema version.
     *
     * @param entityName Instances of what entity should be looked for
     * @return Collection of instances on the current schema version in trash
     */
    Collection getInstancesFromTrash(String entityName);
}
