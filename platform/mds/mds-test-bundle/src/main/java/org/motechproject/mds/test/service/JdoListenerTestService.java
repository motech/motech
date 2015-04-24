package org.motechproject.mds.test.service;


import org.motechproject.mds.annotations.InstanceLifecycleListener;
import org.motechproject.mds.domain.InstanceLifecycleListenerType;
import org.motechproject.mds.test.domain.TestMdsEntity;

public interface JdoListenerTestService {

    @InstanceLifecycleListener(InstanceLifecycleListenerType.POST_CREATE)
    void changeName(TestMdsEntity testMdsEntity);

    @InstanceLifecycleListener(value = InstanceLifecycleListenerType.POST_STORE, packageName = "org.motechproject.mds.test")
    void updateSuperClassString(Object o);
}
