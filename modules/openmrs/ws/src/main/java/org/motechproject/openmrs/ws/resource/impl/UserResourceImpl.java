package org.motechproject.openmrs.ws.resource.impl;

import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.OpenMrsInstance;
import org.motechproject.openmrs.ws.RestClient;
import org.motechproject.openmrs.ws.resource.UserResource;
import org.motechproject.openmrs.ws.resource.model.Person;
import org.motechproject.openmrs.ws.resource.model.Person.PersonSerializer;
import org.motechproject.openmrs.ws.resource.model.Role;
import org.motechproject.openmrs.ws.resource.model.Role.RoleSerializer;
import org.motechproject.openmrs.ws.resource.model.RoleListResult;
import org.motechproject.openmrs.ws.resource.model.User;
import org.motechproject.openmrs.ws.resource.model.UserListResult;
import org.motechproject.openmrs.ws.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class UserResourceImpl implements UserResource {

    private final RestClient restClient;
    private final OpenMrsInstance openmrsInstance;

    @Autowired
    public UserResourceImpl(RestClient restClient, OpenMrsInstance openmrsInstace) {
        this.restClient = restClient;
        this.openmrsInstance = openmrsInstace;
    }

    @Override
    public UserListResult getAllUsers() throws HttpException {
        String responseJson = restClient.getJson(openmrsInstance.toInstancePath("/user?v=full"));
        UserListResult result = (UserListResult) JsonUtils.readJson(responseJson, UserListResult.class);

        return result;
    }

    @Override
    public UserListResult queryForUsersByUsername(String username) throws HttpException {
        String responseJson = restClient.getJson(openmrsInstance.toInstancePathWithParams("/user?q={username}&v=full",
                username));

        UserListResult results = (UserListResult) JsonUtils.readJson(responseJson, UserListResult.class);
        return results;
    }

    @Override
    public User createUser(User user) throws HttpException {
        Gson gson = getGsonWithAdapters();
        String responseJson = restClient.postForJson(openmrsInstance.toInstancePath("/user"), gson.toJson(user));

        User saved = (User) JsonUtils.readJson(responseJson, User.class);
        return saved;
    }

    private Gson getGsonWithAdapters() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Person.class, new PersonSerializer())
                .registerTypeAdapter(Role.class, new RoleSerializer()).create();
        return gson;
    }

    @Override
    public RoleListResult getAllRoles() throws HttpException {
        String responseJson = restClient.getJson(openmrsInstance.toInstancePath("/role?v=full"));
        RoleListResult result = (RoleListResult) JsonUtils.readJson(responseJson, RoleListResult.class);
        return result;
    }

    @Override
    public void updateUser(User user) throws HttpException {
        Gson gson = getGsonWithAdapters();
        String uuid = user.getUuid();
        user.setUuid(null);
        String requestJson = gson.toJson(user);

        restClient.postWithEmptyResponseBody(openmrsInstance.toInstancePathWithParams("/user/{uuid}", uuid),
                requestJson);
    }

}
