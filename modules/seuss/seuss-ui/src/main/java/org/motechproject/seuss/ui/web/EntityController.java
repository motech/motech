package org.motechproject.seuss.ui.web;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.motechproject.seuss.ui.domain.EntityDto;
import org.motechproject.seuss.ui.ex.EntityAlreadyExistException;
import org.motechproject.seuss.ui.ex.EntityNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.apache.commons.lang.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * The <code>EntityController</code> is the Spring Framework Controller used by view layer for
 * executing certain actions on objects.
 *
 * @see SelectData
 * @see SelectResult
 */
@Controller
public class EntityController {
    private static final int MAX_NUMBER_OF_TERMS = 3;
    private List<EntityDto> allEntities = new ArrayList<>();

    @PostConstruct
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    public void init() {
        allEntities.add(new EntityDto("1", "Patient", "OpenMRS", "navio"));
        allEntities.add(new EntityDto("2", "Person", "OpenMRS", "navio"));
        allEntities.add(new EntityDto("3", "Patient", "OpenMRS", "accra"));
        allEntities.add(new EntityDto("4", "Person", "OpenMRS", "accra"));
        allEntities.add(new EntityDto("5", "Appointments", "Appointments"));
        allEntities.add(new EntityDto("6", "Call Log Item", "IVR"));
        allEntities.add(new EntityDto("7", "Voucher"));
        allEntities.add(new EntityDto("8", "Campaign", "Message Campaign"));
    }

    @RequestMapping(value = "/entities", method = RequestMethod.GET)
    @ResponseBody
    public SelectResult getEntities(SelectData data) {
        List<EntityDto> list = new ArrayList<>();

        for (EntityDto entity : allEntities) {
            if (match(data.getTerm(), entity)) {
                list.add(entity);
            }
        }

        Collections.sort(list, new Comparator<EntityDto>() {
            @Override
            public int compare(EntityDto e1, EntityDto e2) {
                return e1.getName().compareToIgnoreCase(e2.getName());
            }
        });

        return new SelectResult(data, list);
    }

    @RequestMapping(value = "/entities/{id}", method = RequestMethod.GET)
    @ResponseBody
    public EntityDto getEntity(@PathVariable final String id) {
        Object found = CollectionUtils.find(allEntities, new Predicate() {
            @Override
            public boolean evaluate(Object entity) {
                return entity instanceof EntityDto
                        && equalsIgnoreCase(((EntityDto) entity).getId(), id);
            }
        });

        if (null == found) {
            throw new EntityNotFoundException();
        }

        return (EntityDto) found;
    }

    @RequestMapping(value = "/entities", method = RequestMethod.POST)
    @ResponseBody
    public EntityDto saveEntity(@RequestBody final EntityDto entity) {
        Object found = CollectionUtils.find(allEntities, new Predicate() {
            @Override
            public boolean evaluate(Object obj) {
                return obj instanceof EntityDto
                        && equalsIgnoreCase(((EntityDto) obj).getName(), entity.getName());
            }
        });

        if (null != found) {
            throw new EntityAlreadyExistException();
        } else {
            entity.setId(String.valueOf(allEntities.size() + 1));
            allEntities.add(entity);

            return entity;
        }
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public void handleEntityNotFoundException(final HttpServletResponse response)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_CONFLICT);

        try (Writer writer = response.getWriter()) {
            writer.write("key:seuss.error.entityNotFound");
        }
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    public void handleEntityAlreadyExistException(final HttpServletResponse response)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_CONFLICT);

        try (Writer writer = response.getWriter()) {
            writer.write("key:seuss.error.entityAlreadyExist");
        }
    }

    private boolean match(String term, EntityDto entity) {
        String name = defaultIfBlank(entity.getName(), "");
        String module = defaultIfBlank(entity.getModule(), "");
        String namespace = defaultIfBlank(entity.getNamespace(), "");
        List<String> terms = new ArrayList<>();
        Integer count = 0;

        for (String t : term.split(",")) {
            if (isNotBlank(t)) {
                terms.add(t.trim());
            }

            if (terms.size() == MAX_NUMBER_OF_TERMS) {
                break;
            }
        }

        if (isNotBlank(term)) {
            for (String t : terms) {
                if (containsIgnoreCase(name, t) || containsIgnoreCase(module, t)
                        || containsIgnoreCase(namespace, t)) {
                    ++count;
                }
            }
        }

        return isBlank(term) || count >= terms.size();
    }
}
