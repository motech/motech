package org.motechproject.security.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.security.constants.HTTPMethod;
import org.motechproject.security.constants.Protocol;
import org.motechproject.security.constants.Scheme;
import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.security.domain.SecurityRuleComparator;
import org.motechproject.security.helper.IDTransformer;
import org.motechproject.security.model.SecurityConfigDto;
import org.motechproject.security.model.SecurityRuleDto;
import org.motechproject.security.repository.AllMotechSecurityRules;
import org.motechproject.security.service.MotechProxyManager;
import org.motechproject.security.service.MotechURLSecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the {@link org.motechproject.security.service.MotechURLSecurityService}
 */
@Service("motechURLSecurityService")
public class MotechURLSecurityServiceImpl implements MotechURLSecurityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MotechURLSecurityServiceImpl.class);

    private AllMotechSecurityRules allSecurityRules;
    private MotechProxyManager proxyManager;

    @Override
    public List<SecurityRuleDto> findAllSecurityRules() {
        List<MotechURLSecurityRule> rules = allSecurityRules.getRules();
        Collections.sort(rules, new SecurityRuleComparator());
        return toSecurityRuleDtoList(rules);
    }

    @Override
    public void updateSecurityConfiguration(SecurityConfigDto configuration) {
        LOGGER.info("Updating security configuration");

        List<MotechURLSecurityRule> newRules = toMotechURLSecurityRuleList(configuration.getSecurityRules());
        Collection newRulesIDs = CollectionUtils.collect(newRules, IDTransformer.INSTANCE);

        for (MotechURLSecurityRule rule : proxyManager.getDefaultSecurityConfiguration().getSecurityRules()) {
            if (!newRulesIDs.contains(rule.getId())) {
                rule.setDeleted(true);
                newRules.add(rule);
            }
        }

        allSecurityRules.addOrUpdate(new MotechSecurityConfiguration(newRules));
        proxyManager.rebuildProxyChain();

        LOGGER.info("Updated security configuration");
    }

    private List<SecurityRuleDto> toSecurityRuleDtoList(List<MotechURLSecurityRule> rules) {
        List<SecurityRuleDto> list = new ArrayList<>();

        if (null != rules) {
            for (MotechURLSecurityRule rule : rules) {
                SecurityRuleDto dto = new SecurityRuleDto();

                dto.setId(rule.getId());
                dto.setActive(rule.isActive());
                dto.setDeleted(rule.isDeleted());
                dto.setOrigin(rule.getOrigin());
                dto.setPattern(rule.getPattern());
                dto.setPriority(rule.getPriority());

                if (null != rule.getProtocol()) {
                    dto.setProtocol(rule.getProtocol().toString());
                }

                dto.setRest(rule.isRest());
                dto.setVersion(rule.getVersion());

                dto.setPermissionAccess(rule.getPermissionAccess());
                dto.setUserAccess(rule.getUserAccess());

                if (null != rule.getMethodsRequired()) {
                    dto.setMethodsRequired(new ArrayList<String>());

                    for (HTTPMethod method : rule.getMethodsRequired()) {
                        dto.getMethodsRequired().add(method.toString());
                    }
                }

                if (null != rule.getSupportedSchemes()) {
                    dto.setSupportedSchemes(new ArrayList<String>());

                    for (Scheme scheme : rule.getSupportedSchemes()) {
                        dto.getSupportedSchemes().add(scheme.toString());
                    }
                }

                list.add(dto);
            }
        }

        return list;
    }

    private List<MotechURLSecurityRule> toMotechURLSecurityRuleList(List<SecurityRuleDto> dtos) {
        List<MotechURLSecurityRule> list = new ArrayList<>();

        if (null != dtos) {
            for (SecurityRuleDto dto : dtos) {
                Long id = dto.getId();

                MotechURLSecurityRule rule = null == id
                        ? new MotechURLSecurityRule()
                        : allSecurityRules.getRuleById(id);

                rule.setActive(dto.isActive());
                rule.setDeleted(dto.isDeleted());
                rule.setOrigin(dto.getOrigin());
                rule.setPattern(dto.getPattern());
                rule.setPriority(dto.getPriority());

                if (null != dto.getProtocol()) {
                    rule.setProtocol(Protocol.valueOf(dto.getProtocol()));
                }

                rule.setRest(dto.isRest());
                rule.setVersion(dto.getVersion());

                rule.setPermissionAccess(dto.getPermissionAccess());
                rule.setUserAccess(dto.getUserAccess());

                if (null != dto.getMethodsRequired()) {
                    rule.setMethodsRequired(new ArrayList<HTTPMethod>());

                    for (String method : dto.getMethodsRequired()) {
                        rule.getMethodsRequired().add(HTTPMethod.valueOf(method));
                    }
                }

                if (null != dto.getSupportedSchemes()) {
                    rule.setSupportedSchemes(new ArrayList<Scheme>());

                    for (String scheme : dto.getSupportedSchemes()) {
                        rule.getSupportedSchemes().add(Scheme.valueOf(scheme));
                    }
                }

                list.add(rule);
            }
        }

        return list;
    }

    @Autowired
    public void setAllSecurityRules(AllMotechSecurityRules allSecurityRules) {
        this.allSecurityRules = allSecurityRules;
    }

    @Autowired
    public void setProxyManager(MotechProxyManager proxyManager) {
        this.proxyManager = proxyManager;
    }
}
