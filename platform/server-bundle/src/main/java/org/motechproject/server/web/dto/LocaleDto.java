package org.motechproject.server.web.dto;

import org.apache.commons.lang.StringUtils;

import java.util.Locale;

/**
 * Dto used for transferring {@link Locale} objects to the web layer.
 */
public class LocaleDto {

    /**
     * The language (en, pl, etc.).
     */
    private String language;

    /**
     * The country for the language, in example US for the english language.
     */
    private String country;

    /**
     * Variant of the language
     */
    private String variant;

    /**
     * Constructs a new instance without setting any of the fields.
     */
    public LocaleDto() {
    }

    /**
     * Constructs a new instance from the provided parameters.
     * @param language the language (en, pl, etc.)
     * @param country the country for the language, in example US for the english language
     * @param variant the variant of the language
     */
    public LocaleDto(String language, String country, String variant) {
        this.language = language;
        this.country = country;
        this.variant = variant;
    }

    /**
     * Constructs an instance from a {@link Locale} object.
     * @param locale the locale to base this dto on
     */
    public LocaleDto(Locale locale) {
        this.language = locale.getLanguage();
        this.country = locale.getCountry();
        this.variant = locale.getVariant();
    }

    /**
     * @return the language (en, pl, etc.)
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language (en, pl, etc.)
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the country for the language, in example US for the english language
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country for the language, in example US for the english language
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return the variant of the language
     */
    public String getVariant() {
        return variant;
    }

    /**
     * @param variant the variant of the language
     */
    public void setVariant(String variant) {
        this.variant = variant;
    }

    /**
     * Builds a {@link Locale} object from the information stored in this dto. The appropriate Locale constructor
     * will be used, based on what data is set.
     * @return the locale represented by this dto
     */
    public Locale toLocale() {
        if (StringUtils.isNotBlank(country) && StringUtils.isNotBlank(variant)) {
            return new Locale(language, country, variant);
        } else if (StringUtils.isNotBlank(country)) {
            return new Locale(language, country);
        } else {
            return new Locale(language);
        }
    }
}
