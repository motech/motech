package org.motechproject.mds.builder.impl;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldSetting;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.domain.TypeSetting;
import org.motechproject.mds.util.Constants;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EnumBuilderImplTest {
    private static final String CLASS_NAME = Constants.Packages.ENTITY + ".Yolo";

    @Test
    public void testBuild() throws Exception {
        String[] expectedValues = {"A", "B", "C", "D", "E"};

        TypeSetting typeSetting = new TypeSetting();
        typeSetting.setName("mds.form.label.values");

        FieldSetting fieldSetting = new FieldSetting(null, typeSetting, StringUtils.join(expectedValues, "\n"));

        Type type = new Type("mds.field.combobox", null, null);

        Field field = new Field(null, "swag", "swag", false, false, null, null, null);
        field.setType(type);
        field.addSetting(fieldSetting);

        Entity entity = new Entity(CLASS_NAME);
        entity.addField(field);

        List<ClassData> list = new EnumBuilderImpl().build(entity);

        assertFalse("At one enum should be created", list.isEmpty());

        ClassData data = list.get(0);

        MDSClassLoader.getInstance().defineClass(data.getClassName(), data.getBytecode());
        Class<?> enumClass = MDSClassLoader.getInstance().loadClass(data.getClassName());

        assertTrue("The class definition should be enum", enumClass.isEnum());

        Object[] enumConstants = enumClass.getEnumConstants();

        String[] actualValues = new String[enumConstants.length];

        for (int i = 0; i < enumConstants.length; i++) {
            actualValues[i] = enumConstants[i].toString();
        }

        Arrays.sort(expectedValues, String.CASE_INSENSITIVE_ORDER);
        Arrays.sort(actualValues, String.CASE_INSENSITIVE_ORDER);

        assertArrayEquals(expectedValues, actualValues);
    }

}
