package org.motechproject.commons.api;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CsvConverterTest {

    @Test
    public void shouldProperlyConvertToCSV() {
        List list = new ArrayList<List>();

        list.add(asList("A","B","C"));
        list.add(asList("John", "Smith", "NY"));
        list.add(asList("Some longer text, that has got commas included in it", "\"Make love not war\""));

        String result = CsvConverter.convertToCSV(list);

        String expected = "A,B,C\r\nJohn,Smith,NY\r\n\"Some longer text, that has got commas included in it\",\"\"\"Make love not war\"\"\"\r\n";

        assertThat(result, is(expected));
    }
}
