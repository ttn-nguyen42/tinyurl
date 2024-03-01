package org.ntranlab.url.helpers.utils;

import jakarta.servlet.http.HttpServletRequest;

public class ServletHelpers {
    public static String getRequestIp(HttpServletRequest request) {
        String forwardedIp = request.getHeader("X-Forwarded-For");
        if (forwardedIp == null || forwardedIp.isBlank()) {
            return request.getRemoteAddr();
        }
        return forwardedIp;
    }

    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
