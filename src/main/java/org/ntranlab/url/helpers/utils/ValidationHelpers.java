package org.ntranlab.url.helpers.utils;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ValidationHelpers {
    private final UrlValidator validator;
    private static String ALPHANUMERIC = "^[a-zA-Z0-9]*$";
    private final Pattern alphanumericPattern;

    public ValidationHelpers() {
        this.validator = new UrlValidator();
        this.alphanumericPattern = Pattern.compile(ALPHANUMERIC);
    }

    public boolean isValidUrl(String url) {
        return this.validator.
                isValid(url);
    }

    public boolean isValidAlias(String alias) {
        if (alias.isEmpty()) {
            return false;
        }
        Matcher matcher = alphanumericPattern.matcher(alias);
        return matcher.matches();
    }
}
