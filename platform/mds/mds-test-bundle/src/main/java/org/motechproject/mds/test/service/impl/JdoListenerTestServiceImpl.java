package org.motechproject.mds.test.service.impl;

import org.motechproject.mds.test.domain.SuperClass;
import org.motechproject.mds.test.domain.TestMdsEntity;
import org.motechproject.mds.test.service.instancelifecyclelistener.JdoListenerTestService;
import org.springframework.stereotype.Service;

@Service("jdoListenerTestService")
public class JdoListenerTestServiceImpl implements JdoListenerTestService {

    @Override
    public void changeName(TestMdsEntity testMdsEntity) {
        if ("TestChangeName".equals(testMdsEntity.getSomeString())) {
            testMdsEntity.setSomeString("NameWasChanged");
        }
    }

    @Override
    public void updateSuperClassString(Object o) {
        if (o instanceof SuperClass) {
            SuperClass sc = (SuperClass) o;
            sc.setSuperClassString("StringWasChanged");
        }
    }
}
