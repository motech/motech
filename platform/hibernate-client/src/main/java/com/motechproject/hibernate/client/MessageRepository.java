package com.motechproject.hibernate.client;

import com.motechproject.hibernate.domain.Message;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository {

    @Transactional
    List<String> message();

    Integer saveMessage(Message message);
}
