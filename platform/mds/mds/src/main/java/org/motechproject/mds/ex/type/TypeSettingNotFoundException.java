package org.motechproject.mds.ex.type;

/**
 * The <code>TypeSettingNotFoundException</code> exception signals a situation in which a type setting
 * for given type does not exists in database.
 */
public class TypeSettingNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -7082846400094214836L;

    public TypeSettingNotFoundException(String message) {
        super(message);
    }
}
