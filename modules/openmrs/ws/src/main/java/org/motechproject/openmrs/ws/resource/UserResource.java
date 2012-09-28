package org.motechproject.openmrs.ws.resource;

import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.model.RoleListResult;
import org.motechproject.openmrs.ws.resource.model.User;
import org.motechproject.openmrs.ws.resource.model.UserListResult;

public interface UserResource {

    UserListResult getAllUsers() throws HttpException;

    UserListResult queryForUsersByUsername(String username) throws HttpException;

    User createUser(User user) throws HttpException;

    void updateUser(User user) throws HttpException;

    RoleListResult getAllRoles() throws HttpException;
}
