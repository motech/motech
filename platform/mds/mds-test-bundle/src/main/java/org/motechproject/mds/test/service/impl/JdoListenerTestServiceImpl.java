package org.motechproject.mds.test.service.impl;

import org.motechproject.mds.test.domain.TestMdsEntity;
import org.motechproject.mds.test.service.JdoListenerTestService;
import org.springframework.stereotype.Service;

@Service("jdoListenerTestService")
public class JdoListenerTestServiceImpl implements JdoListenerTestService {

    @Override
    public void changeName(TestMdsEntity testMdsEntity) {
        if (testMdsEntity.getSomeString().equals("TestChangeName")) {
            testMdsEntity.setSomeString("NameWasChanged");
        }
    }
}
