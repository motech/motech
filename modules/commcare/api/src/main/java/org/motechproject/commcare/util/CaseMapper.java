package org.motechproject.commcare.util;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;
import org.motechproject.commcare.domain.CaseXml;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: pchandra
 * Date: 3/24/12
 * Time: 10:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class CaseMapper<T> {

    private Class<T> clazz;

    public CaseMapper(Class<T> clazz) {
        this.clazz = clazz;
    }


    public T mapToDomainObject(CaseXml ccCase) {
        T instance = null;
        try {
            instance = clazz.newInstance();
            BeanUtils.copyProperties(instance, ccCase);
            BeanUtils.populate(instance, ccCase.getFieldValues());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instance;

    }

    public CaseXml mapFromDomainObject(T careCase) {
        CaseXml ccCase = new CaseXml();
        try {
            BeanUtils.copyProperties(ccCase, careCase);

            BeanMap beanMap = new BeanMap(careCase);
            removeStaticProperties(beanMap);

            Map<String, String> valueMap = new HashMap<String, String>();
            while (beanMap.keyIterator().hasNext()) {
                valueMap.put((String) beanMap.keyIterator().next(), (String) beanMap.get((String) beanMap.keyIterator().next()));
            }
            ccCase.setFieldValues(valueMap);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ccCase;
    }

    private void removeStaticProperties(BeanMap beanMap) {
        beanMap.remove("case_id");
        beanMap.remove("api_key");
        beanMap.remove("date_modified");
        beanMap.remove("case_type");
        beanMap.remove("case_name");
        beanMap.remove("owner_id");
    }
}
