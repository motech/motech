package org.motechproject.mobileforms.api.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Form {
    private Integer formId;
    private String name;
    private String fileName;
    private String content;

    public Form(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
    }

    public Form(String name, String fileName, String content) {
        this.name = name;
        this.fileName = fileName;
        this.content = content;
        this.formId = extractId(content);
    }

    private Integer extractId(String content){
       Matcher matcher = Pattern.compile("<xf:xforms.*?id=\"(.*?)\"", Pattern.CASE_INSENSITIVE).matcher(content);
       Integer formId = null;
       if (matcher.find()) {
           String formIdString = matcher.group(1);
           formId = Integer.valueOf(formIdString);
       }
       return formId;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }


    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Form)) return false;

        Form form = (Form) o;

        if (content != null ? !content.equals(form.content) : form.content != null) return false;
        if (fileName != null ? !fileName.equals(form.fileName) : form.fileName != null) return false;
        if (name != null ? !name.equals(form.name) : form.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }

    public Integer formId() {
        return formId;
    }
}
