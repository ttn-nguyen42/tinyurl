package org.ntranlab.url.helpers.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;

@Component
public class ValidationHelpers {
    private final UrlValidator validator;
    private static String ALIAS = "^[a-zA-Z0-9-@]*$";
    private final Pattern aliasPattern;

    public ValidationHelpers() {
        this.validator = new UrlValidator();
        this.aliasPattern = Pattern.compile(ALIAS);
    }

    public boolean isValidUrl(String url) {
        return this.validator.isValid(url);
    }

    public boolean isValidAlias(String alias) {
        if (alias.isEmpty()) {
            return false;
        }
        Matcher matcher = aliasPattern.matcher(alias);
        return matcher.matches();
    }
}
