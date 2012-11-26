package org.motechproject.commcare.util;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;
import org.motechproject.commcare.domain.CaseXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CaseMapper<T> {

    private static final Logger LOG = LoggerFactory.getLogger(CaseMapper.class);

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
            LOG.error(e.getMessage(), e);
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
            LOG.error(e.getMessage(), e);
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
