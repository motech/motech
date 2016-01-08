package org.motechproject.config.core.domain;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.io.File;
import java.util.List;

public class FileListMatcher extends BaseMatcher<List<File>> {

    private final boolean isFileExpected;
    private String fileName;

    private FileListMatcher(String fileName, boolean isFileExpected) {
        this.fileName = fileName;
        this.isFileExpected = isFileExpected;
    }

    public static FileListMatcher has(String fileName) {
        return new FileListMatcher(fileName, true);
    }

    public static FileListMatcher doesNotHave(String fileName) {
        return new FileListMatcher(fileName, false);
    }

    @Override
    public boolean matches(Object item) {
        List<File> actualFiles = (List<File>) item;

        for (File actualFile : actualFiles) {
            if (actualFile.getName().equals(fileName)) {
                return isFileExpected;
            }
        }
        return !isFileExpected;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(String.format("Expected file %s in list", fileName));
    }
}
