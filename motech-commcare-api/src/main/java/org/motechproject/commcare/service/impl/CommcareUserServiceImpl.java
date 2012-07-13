package org.motechproject.commcare.service.impl;

import java.lang.reflect.Type;
import java.util.List;
import org.motechproject.commcare.domain.CommcareUser;
import org.motechproject.commcare.domain.CommcareUsersJson;
import org.motechproject.commcare.service.CommcareUserService;
import org.motechproject.commcare.util.CommCareAPIHttpClient;
import org.motechproject.dao.MotechJsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.reflect.TypeToken;

@Service
public class CommcareUserServiceImpl implements CommcareUserService {

    private MotechJsonReader motechJsonReader;

    private CommCareAPIHttpClient commcareHttpClient;

    @Autowired
    public CommcareUserServiceImpl(CommCareAPIHttpClient commcareHttpClient) {
        this.commcareHttpClient = commcareHttpClient;
        this.motechJsonReader = new MotechJsonReader();
    }

    @Override
    public List<CommcareUser> getAllUsers() {

        String response = commcareHttpClient.usersRequest();

        Type commcareUserType = new TypeToken<CommcareUsersJson>() {
        } .getType();

        CommcareUsersJson allUsers = (CommcareUsersJson) motechJsonReader
                .readFromString(response, commcareUserType);

        return allUsers.getObjects();

    }

    @Override
    public CommcareUser getCommcareUserById(String id) {
        List<CommcareUser> userList = getAllUsers();
        for (CommcareUser user : userList) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }
}
