package org.motechproject.mobileforms.api.service;

import org.motechproject.mobileforms.api.domain.FormGroup;

import java.util.List;
import java.util.Map;


/**
 * \defgroup MobileForms Mobile Forms
 */


/**
 * \ingroup MobileForms
 * Mobile forms service interface, allows listing available mobile form groups.
 * and get form details.
 */

public interface MobileFormsService {
    /**
     * Get all form group names (study) along with index in an object array, first object is index and second one is group name
     * Object array is used in serializing this information to device.
     * @return List of new Object[] {1, "Group1"},
     */
    List<Object[]> getAllFormGroups();

    /**
     * Get all forms for the form group.
     * @see FormGroup
     * @param formGroupIndex Index returned by {@link #getAllFormGroups()}
     * @return FormGroup
     */
    FormGroup getForms(Integer formGroupIndex);

    /**
     * Get All forms' content as map of id, content. Id defined in xform file as an attribute to xforms tag.
     * @return
     */
    Map<Integer, String> getFormIdMap();
}
