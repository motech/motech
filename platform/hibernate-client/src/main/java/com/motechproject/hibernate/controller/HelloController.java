package com.motechproject.hibernate.controller;


import com.motechproject.hibernate.client.MessageRepository;
import com.motechproject.hibernate.domain.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@Controller
public class HelloController {

    @Autowired
    private MessageRepository messageRepository;

    @RequestMapping(value = "message", method = RequestMethod.GET)
    public List<String> hello() {
        List<String> message = messageRepository.message();
        return message;
    }

    @RequestMapping(value = "message", method = RequestMethod.POST)
    @ResponseBody
    public Integer saveMessage() {
        Message message = new Message();
        message.setText(UUID.randomUUID().toString());
        Integer id = messageRepository.saveMessage(message);
        return id;
    }

}
