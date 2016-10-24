package org.motechproject.server.web.dto;

import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;

import java.util.Locale;
import java.util.NavigableMap;

/**
 * Class responsible for holding data shared between startup controller and its view
 */
public class StartupViewData {
    private StartupSuggestionsForm suggestions;
    private StartupForm startupSettings;
    private NavigableMap<String, String> languages;
    private Boolean isFileMode;
    private Locale pageLang;
    private Boolean redirectHome;
    private Boolean requireConfigFiles;
    private Boolean isAdminRegistered;

    public StartupSuggestionsForm getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(StartupSuggestionsForm suggestions) {
        this.suggestions = suggestions;
    }

    public StartupForm getStartupSettings() {
        return startupSettings;
    }

    public void setStartupSettings(StartupForm startupSettings) {
        this.startupSettings = startupSettings;
    }

    public NavigableMap<String, String> getLanguages() {
        return languages;
    }

    public void setLanguages(NavigableMap<String, String> languages) {
        this.languages = languages;
    }

    public Boolean getIsFileMode() {
        return isFileMode;
    }

    public void setIsFileMode(Boolean isFileMode) {
        this.isFileMode = isFileMode;
    }

    public Locale getPageLang() {
        return pageLang;
    }

    public void setPageLang(Locale pageLang) {
        this.pageLang = pageLang;
    }

    public Boolean getRedirectHome() {
        return redirectHome;
    }

    public void setRedirectHome(Boolean redirectHome) {
        this.redirectHome = redirectHome;
    }

    public Boolean getRequireConfigFiles() {
        return requireConfigFiles;
    }

    public void setRequireConfigFiles(Boolean requireConfigFiles) {
        this.requireConfigFiles = requireConfigFiles;
    }

    public Boolean getIsAdminRegistered() {
        return isAdminRegistered;
    }

    public void setIsAdminRegistered(Boolean isAdminRegistered) {
        this.isAdminRegistered = isAdminRegistered;
    }
}
