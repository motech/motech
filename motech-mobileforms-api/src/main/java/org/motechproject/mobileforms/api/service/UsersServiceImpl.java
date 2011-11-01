package org.motechproject.mobileforms.api.service;

import org.motechproject.mobileforms.api.utils.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class UsersServiceImpl implements UsersService {
    public static final String FORMS_USER_ACCOUNTS = "forms.user.accounts";
    private Properties properties;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UsersServiceImpl(@Qualifier(value = "mobileFormsProperties") Properties properties, PasswordEncoder passwordEncoder) {
        this.properties = properties;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Object[]> getUsers(){
        String[] userAccounts = properties.getProperty(FORMS_USER_ACCOUNTS).split(",");
        List<Object[]> users = new ArrayList<Object[]>();
        for (int i=0; i < userAccounts.length; i++) {
            String[] userDetails = userAccounts[i].split("\\|");
            String userName = userDetails[0];
            String password = userDetails[1];
            String salt = userDetails[2];
            users.add(new Object[]{i+1, userName, passwordEncoder.sha(password, salt), salt});
        }
        return users;
    }
}
