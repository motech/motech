package motech.archetype.service;

import org.springframework.stereotype.Service;

@Service("helloWorldService")
public class HelloWorldServiceImpl implements HelloWorldService {

    @Override
    public String getMessage() {
        return "Hello World";
    }

}
