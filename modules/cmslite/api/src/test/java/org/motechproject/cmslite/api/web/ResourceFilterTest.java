package org.motechproject.cmslite.api.web;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Test;
import org.motechproject.cmslite.api.model.Content;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.model.StringContent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.motechproject.cmslite.api.web.ResourceFilter.equalsContent;
import static org.motechproject.cmslite.api.web.ResourceFilter.getContentType;

public class ResourceFilterTest {
    private class TestContent extends Content {
        private static final long serialVersionUID = -7200151247736237000L;
    }

    @Test
    public void shouldReturnAll() {
        List<Content> contents = getContents();
        List<ResourceDto> expected = createInputData(contents);
        List<ResourceDto> actual = ResourceFilter.filter(createGridSettings("", true, true, ""), contents);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnOnlyStringResources() {
        List<Content> contents = getContents();
        List<ResourceDto> expected = createInputData(contents);

        CollectionUtils.filter(expected, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return object instanceof ResourceDto &&
                        !equalsIgnoreCase(((ResourceDto) object).getType(), "stream");
            }
        });

        List<ResourceDto> actual = ResourceFilter.filter(createGridSettings("", true, false, ""), contents);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnOnlyStreamResources() {
        List<Content> contents = getContents();
        List<ResourceDto> expected = createInputData(contents);

        CollectionUtils.filter(expected, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return object instanceof ResourceDto &&
                        !equalsIgnoreCase(((ResourceDto) object).getType(), "string");
            }
        });

        List<ResourceDto> actual = ResourceFilter.filter(createGridSettings("", false, true, ""), contents);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnOnlyWithGivenName() {
        final String givenName = "files";

        List<Content> contents = getContents();
        List<ResourceDto> expected = createInputData(contents);

        CollectionUtils.filter(expected, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return object instanceof ResourceDto &&
                        startsWithIgnoreCase(((ResourceDto) object).getName(), givenName);
            }
        });

        List<ResourceDto> actual = ResourceFilter.filter(createGridSettings(givenName, true, true, ""), contents);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnOnlyWithGivenLanguages() {
        final Set<String> givenLanguages = new HashSet<>(asList("english", "spanish"));

        List<Content> contents = getContents();
        List<ResourceDto> expected = new ArrayList<>();
        expected.add(new ResourceDto("files-string-1", "string", "english"));
        expected.add(new ResourceDto("file-string-2", "string", "english", "spanish"));

        List<ResourceDto> actual = ResourceFilter.filter(createGridSettings("", true, true, "english,spanish"), contents);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnEmptyList() {
        List<Content> contents = getContents();
        contents.add(new TestContent());

        List<ResourceDto> expected = new ArrayList<>();
        List<ResourceDto> actual = ResourceFilter.filter(createGridSettings("", false, false, ""), contents);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    private List<Content> getContents() {
        List<Content> list = new ArrayList<>();

        list.add(new StringContent("english", "files-string-1", "some valid value"));
        list.add(new StringContent("polish", "files-string-1", "some valid value"));
        list.add(new StringContent("english", "file-string-2", "some valid value"));
        list.add(new StringContent("spanish", "file-string-2", "some valid value"));
        list.add(new StringContent("german", "files-string-3", "some valid value"));

        list.add(new StreamContent("polish", "file-stream-1", null, "checksum", "contentType"));
        list.add(new StreamContent("danish", "files-stream-2", null, "checksum", "contentType"));
        list.add(new StreamContent("arabic", "file-stream-3", null, "checksum", "contentType"));

        return list;
    }

    private List<ResourceDto> createInputData(List<Content> contents) {
        List<ResourceDto> list = new ArrayList<>(contents.size());

        for (final Content content : contents) {
            ResourceDto dto = (ResourceDto) CollectionUtils.find(list, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return (object instanceof ResourceDto) &&
                            equalsContent((ResourceDto) object, content.getName(), getContentType(content));
                }
            });

            if (dto == null) {
                list.add(new ResourceDto(content));
            } else {
                dto.addLanguage(content.getLanguage());
            }
        }

        return list;
    }

    private GridSettings createGridSettings(String name, Boolean string, Boolean stream, String languages) {
        GridSettings settings = new GridSettings();
        settings.setLanguages(languages);
        settings.setName(name);
        settings.setStream(stream);
        settings.setString(string);

        return settings;
    }
}
