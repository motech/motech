package com.motechproject.hibernate.client;


import com.motechproject.hibernate.domain.Message;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MessageRepositoryImpl implements MessageRepository {

    @Autowired
    @Qualifier("hibernateSessionFactory")
    private SessionFactory sessionFactory;

    @Override
    public List<String> message() {
        Session currentSession = currentSession();
        Query query = currentSession.createQuery("from com.motechproject.hibernate.domain.Message");
        List list = query.list();
        List<String> values = new ArrayList<>();
        values.add("test message");
        for (Object o : list) {
            Message message = (Message) o;
            values.add(message.getText());
        }
        return values;
    }

    @Override
    public Integer saveMessage(Message message) {
        Session currentSession = currentSession();
        currentSession.save(message);
        return message.getId();
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

}
