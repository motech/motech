package org.motechproject.mobileforms.api.domain;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents Form definition, typically read from json configuration file. bean is class that extends {@link FormBean} to handle xml data.
 */
public class Form {
    private Integer id;
    private String name;
    private String bean;
    private String content;
    private String fileName;
    private List<String> depends;
    private String studyName;
    private String validator;

    public static final String XF_XFORMS_ID = "<xf:xforms.*?id=\"(.*?)\"";

    public Form() {
        // needed by gson
    }

    public Form(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
    }

    public Form(String name, String fileName, String content, String bean, String validator, String studyName, List<String> depends) {
        this(name, fileName);
        this.content = content;
        this.bean = bean;
        this.validator = validator;
        this.studyName = studyName;
        this.depends = depends;
        this.id = extractId(content);
    }

    public String name() {
        return name;
    }

    public String fileName() {
        return fileName;
    }

    public Integer id() {
        return id;
    }

    public String content() {
        return content;
    }

    public String bean() {
        return bean;
    }

    public String validator() {
        return validator;
    }

    public List<String> getDepends() {
        return depends;
    }

    private Integer extractId(String content) {
        Matcher matcher = Pattern.compile(XF_XFORMS_ID, Pattern.CASE_INSENSITIVE).matcher(content);
        Integer formId = null;
        if (matcher.find()) {
            String formIdString = matcher.group(1);
            formId = Integer.valueOf(formIdString);
        }
        return formId;
    }

    public String studyName() {
        return studyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Form)) {
            return false;
        }

        Form form = (Form) o;

        return Objects.equals(bean, form.bean) && Objects.equals(content, form.content) &&
                Objects.equals(depends, form.depends) && Objects.equals(fileName, form.fileName) &&
                Objects.equals(id, form.id) && Objects.equals(name, form.name) &&
                Objects.equals(studyName, form.studyName) && Objects.equals(validator, form.validator);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (bean != null ? bean.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (depends != null ? depends.hashCode() : 0);
        result = 31 * result + (studyName != null ? studyName.hashCode() : 0);
        result = 31 * result + (validator != null ? validator.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Form{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", bean='" + bean + '\'' +
                ", content='" + content + '\'' +
                ", fileName='" + fileName + '\'' +
                ", depends=" + depends +
                ", studyName='" + studyName + '\'' +
                ", validator='" + validator + '\'' +
                '}';
    }
}
