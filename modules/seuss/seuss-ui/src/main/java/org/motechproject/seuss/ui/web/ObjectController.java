package org.motechproject.seuss.ui.web;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.motechproject.seuss.ui.domain.ObjectDto;
import org.motechproject.seuss.ui.ex.ObjectAlreadyExistException;
import org.motechproject.seuss.ui.ex.ObjectNotFoundException;
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
 * The <code>ObjectController</code> is the Spring Framework Controller used by view layer for
 * executing certain actions on objects.
 *
 * @see SelectData
 * @see SelectResult
 */
@Controller
public class ObjectController {
    private static final int MAX_NUMBER_OF_TERMS = 3;
    private List<ObjectDto> allObjects = new ArrayList<>();

    @PostConstruct
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    public void init() {
        allObjects.add(new ObjectDto("1", "Patient", "OpenMRS", "navio"));
        allObjects.add(new ObjectDto("2", "Person", "OpenMRS", "navio"));
        allObjects.add(new ObjectDto("3", "Patient", "OpenMRS", "accra"));
        allObjects.add(new ObjectDto("4", "Person", "OpenMRS", "accra"));
        allObjects.add(new ObjectDto("5", "Appointments", "Appointments"));
        allObjects.add(new ObjectDto("6", "Call Log Item", "IVR"));
        allObjects.add(new ObjectDto("7", "Voucher"));
        allObjects.add(new ObjectDto("8", "Campaign", "Message Campaign"));
    }

    @RequestMapping(value = "/objects", method = RequestMethod.GET)
    @ResponseBody
    public SelectResult getObjects(SelectData data) {
        List<ObjectDto> list = new ArrayList<>();

        for (ObjectDto object : allObjects) {
            if (match(data.getTerm(), object)) {
                list.add(object);
            }
        }

        Collections.sort(list, new Comparator<ObjectDto>() {
            @Override
            public int compare(ObjectDto o1, ObjectDto o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        return new SelectResult(data, list);
    }

    @RequestMapping(value = "/objects/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ObjectDto getObject(@PathVariable final String id) {
        Object found = CollectionUtils.find(allObjects, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return object instanceof ObjectDto
                        && equalsIgnoreCase(((ObjectDto) object).getId(), id);
            }
        });

        if (null == found) {
            throw new ObjectNotFoundException();
        }

        return (ObjectDto) found;
    }

    @RequestMapping(value = "/objects", method = RequestMethod.POST)
    @ResponseBody
    public ObjectDto saveObject(@RequestBody final ObjectDto object) {
        Object found = CollectionUtils.find(allObjects, new Predicate() {
            @Override
            public boolean evaluate(Object obj) {
                return obj instanceof ObjectDto
                        && equalsIgnoreCase(((ObjectDto) obj).getName(), object.getName());
            }
        });

        if (null != found) {
            throw new ObjectAlreadyExistException();
        } else {
            object.setId(String.valueOf(allObjects.size() + 1));
            allObjects.add(object);

            return object;
        }
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public void handleObjectNotFoundException(final HttpServletResponse response)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_CONFLICT);

        try (Writer writer = response.getWriter()) {
            writer.write("key:seuss.error.objectNotFound");
        }
    }

    @ExceptionHandler(ObjectAlreadyExistException.class)
    public void handleObjectAlreadyExistException(final HttpServletResponse response)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_CONFLICT);

        try (Writer writer = response.getWriter()) {
            writer.write("key:seuss.error.objectAlreadyExist");
        }
    }

    private boolean match(String term, ObjectDto object) {
        String name = defaultIfBlank(object.getName(), "");
        String module = defaultIfBlank(object.getModule(), "");
        String namespace = defaultIfBlank(object.getNamespace(), "");
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
