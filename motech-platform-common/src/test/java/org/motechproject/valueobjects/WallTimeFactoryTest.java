package org.motechproject.valueobjects;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.valueobjects.factory.WallTimeFactory;

public class WallTimeFactoryTest {
    @Test
    public void create() {
        Assert.assertEquals(wallTime(1, WallTimeUnit.Day), user("1 Day"));
        Assert.assertEquals(wallTime(1, WallTimeUnit.Day), user("1 day"));
        Assert.assertEquals(wallTime(1, WallTimeUnit.Day), user(" 1 day "));
        Assert.assertEquals(wallTime(2, WallTimeUnit.Day), user(" 2 days "));
        Assert.assertEquals(wallTime(2, WallTimeUnit.Week), user(" 2 weeks "));
    }

    private WallTime user(String userReadableForm) {
        return WallTimeFactory.create(userReadableForm);
    }

    private WallTime wallTime(int value, WallTimeUnit unit) {
        return new WallTime(value, unit);
    }
}
