package org.motechproject.openmrs.ws.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.model.Password;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.UserResource;
import org.motechproject.openmrs.ws.resource.model.Role;
import org.motechproject.openmrs.ws.resource.model.RoleListResult;
import org.motechproject.openmrs.ws.resource.model.User;
import org.motechproject.openmrs.ws.resource.model.UserListResult;
import org.motechproject.openmrs.ws.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component("userAdapter")
public class MRSUserAdapterImpl implements MRSUserAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MRSUserAdapterImpl.class);

    private static final int DEFAULT_PASSWORD_LENGTH = 8;
    private final Map<String, String> cachedRoles = new HashMap<String, String>();

    private final UserResource userResource;
    private final MRSPersonAdapterImpl personAdapter;
    private final Password password = new Password(DEFAULT_PASSWORD_LENGTH);

    @Autowired
    public MRSUserAdapterImpl(UserResource userResource, MRSPersonAdapterImpl personAdapter) {
        this.userResource = userResource;
        this.personAdapter = personAdapter;
    }

    @Override
    public void changeCurrentUserPassword(String currentPassword, String newPassword) {
        // no way of doing this operation because you cannot retrieve the
        // password for a user through the web services
        throw new UnsupportedOperationException();
    }

    @Override
    public List<MRSUser> getAllUsers() {
        UserListResult result = null;
        try {
            result = userResource.getAllUsers();
        } catch (HttpException e) {
            LOGGER.error("Failed to get all users from OpenMRS: " + e.getMessage());
            return Collections.emptyList();
        }

        List<User> users = result.getResults();
        List<MRSUser> mrsUsers = new ArrayList<MRSUser>();
        for (User u : users) {
            // OpenMRS provides 2 default users (admin/daemon)
            // Intentionally filtering out these users as they have missing
            // properties e.g. daemon has no associated person
            if ("admin".equals(u.getSystemId()) || "daemon".equals(u.getSystemId())) {
                continue;
            }
            // the user response does not include the full person name or
            // address
            // must retrieve these separately
            MRSPerson person = personAdapter.getPerson(u.getPerson().getUuid());
            mrsUsers.add(convertToMrsUser(u, person));
        }

        return mrsUsers;
    }

    private MRSUser convertToMrsUser(User u, MRSPerson person) {
        MRSUser user = new MRSUser();
        user.id(u.getUuid());
        user.person(person);
        user.securityRole(u.getFirstRole());
        user.systemId(u.getSystemId());
        user.userName(u.getUsername());

        return user;
    }

    @Override
    public MRSUser getUserByUserName(String username) {
        Validate.notEmpty(username, "Username cannot be empty");

        UserListResult results = null;
        try {
            results = userResource.queryForUsersByUsername(username);
        } catch (HttpException e) {
            LOGGER.error("Failed to retrieve user by username: " + username + " with error: " + e.getMessage());
            return null;
        }

        if (results.getResults().size() == 0) {
            return null;
        } else if (results.getResults().size() > 1) {
            LOGGER.warn("Found multipe user accounts");
        }

        // User response json does not include person name or address
        // must retrieve these separately
        User u = results.getResults().get(0);
        MRSPerson person = personAdapter.getPerson(u.getPerson().getUuid());
        MRSUser user = convertToMrsUser(u, person);

        return user;
    }

    @Override
    public Map<String, Object> saveUser(MRSUser user) throws UserAlreadyExistsException {
        Validate.notNull(user, "User cannot be null");
        Validate.notEmpty(user.getUserName(), "Username cannot be empty");
        Validate.notNull(user.getPerson(), "Person cannot be null");

        if (getUserByUserName(user.getUserName()) != null) {
            LOGGER.warn("Already found user with username: " + user.getUserName());
            throw new UserAlreadyExistsException();
        }

        // attempt to retrieve the roleUuid before saving the person
        // its possible the role doesn't exist in the OpenMRS, in which case
        // an exception is thrown. If the person is saved prior to checking
        // for the role, there would need to be another call to delete the
        // person
        // otherwise it would leave the OpenMRS in an inconsistent state
        getRoleUuidByRoleName(user);

        MRSPerson savedPerson = personAdapter.savePerson(user.getPerson());
        user.person(savedPerson);

        String generatedPassword = password.create();
        User converted = convertToUser(user, generatedPassword);
        User saved = null;
        try {
            saved = userResource.createUser(converted);
        } catch (HttpException e) {
            LOGGER.error("Failed to save user: " + e.getMessage());
            return null;
        }

        user.id(saved.getUuid());
        user.systemId(saved.getSystemId());
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(USER_KEY, user);
        values.put(PASSWORD_KEY, generatedPassword);

        return values;
    }

    private User convertToUser(MRSUser user, String password) {
        User converted = new User();
        converted.setPassword(password);
        converted.setPerson(ConverterUtils.convertToPerson(user.getPerson(), false));
        converted.setRoles(convertRoles(user));
        converted.setUsername(user.getUserName());
        converted.setSystemId(user.getSystemId());
        converted.setUuid(user.getId());
        return converted;
    }

    private List<Role> convertRoles(MRSUser user) {
        List<Role> roles = new ArrayList<Role>();
        Role role = new Role();
        role.setUuid(getRoleUuidByRoleName(user));
        roles.add(role);
        return roles;
    }

    private String getRoleUuidByRoleName(MRSUser user) {
        if (!roleIsPresentInOpenMrs(user.getSecurityRole())) {
            LOGGER.error("Could not find a role in OpenMRS with name: " + user.getSecurityRole());
            throw new MRSException(new RuntimeException("No OpenMRS role found with name: " + user.getSecurityRole()));
        }

        String roleUuid = cachedRoles.get(user.getSecurityRole());
        return roleUuid;
    }

    private boolean roleIsPresentInOpenMrs(String securityRole) {
        if (cachedRoles.containsKey(securityRole)) {
            return true;
        }

        populateRoleCache();
        return cachedRoles.containsKey(securityRole);
    }

    private void populateRoleCache() {
        RoleListResult result = null;
        try {
            result = userResource.getAllRoles();
        } catch (HttpException e) {
            LOGGER.error("Failed to retrieve the list of roles: " + e.getMessage());
            return;
        }

        cachedRoles.putAll(handleResult(result.getResults()));
    }

    private Map<String, String> handleResult(List<Role> results) {
        Map<String, String> roleMap = new HashMap<String, String>();
        for (Role role : results) {
            roleMap.put(role.getName(), role.getUuid());
        }

        return roleMap;
    }

    @Override
    public String setNewPasswordForUser(String username) {
        Validate.notEmpty(username, "Username cannot be empty");

        MRSUser user = getUserByUserName(username);
        if (user == null) {
            LOGGER.warn("No user foudn with username: " + username);
            throw new UsernameNotFoundException("No user found with username: " + username);
        }

        String newPassword = password.create();

        User tmp = new User();
        tmp.setPassword(newPassword);
        tmp.setUuid(user.getId());

        try {
            userResource.updateUser(tmp);
        } catch (HttpException e) {
            LOGGER.error("Failed to set password for username: " + username);
            return null;
        }

        return newPassword;
    }

    @Override
    public Map<String, Object> updateUser(MRSUser user) {
        Validate.notNull(user, "User cannot be null");
        Validate.notEmpty(user.getId(), "User id cannot be empty");
        Validate.notNull(user.getPerson(), "User Person cannot be null");
        Validate.notEmpty(user.getPerson().getId(), "User person id cannot be empty");

        personAdapter.updatePerson(user.getPerson());

        String generatedPassword = password.create();
        User converted = convertToUser(user, generatedPassword);

        try {
            userResource.updateUser(converted);
        } catch (HttpException e) {
            LOGGER.error("Failed to update user: " + user.getId());
            return null;
        }

        Map<String, Object> values = new HashMap<String, Object>();
        values.put(USER_KEY, user);
        values.put(PASSWORD_KEY, generatedPassword);

        return values;
    }
}
