package org.ntranlab.url.routes.web;

import org.ntranlab.url.business.routers.RouterService;
import org.ntranlab.url.helpers.utils.ServletHelpers;
import org.ntranlab.url.models.redirect.RedirectRequest;
import org.ntranlab.url.models.redirect.RedirectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class WebController {
    private final RouterService routerService;

    private Logger logger = LoggerFactory.getLogger(WebController.class);

    public WebController(final RouterService routerService) {
        this.routerService = routerService;
    }

    /**
     * Returns the front page of the application
     * 
     * @param model
     * @return
     */
    @GetMapping(value = "/")
    public String homePage(Model model) {
        return "index";
    }

    @GetMapping(value = "/to/{siteId}")
    public String redirectPage(
            @PathVariable(name = "siteId", required = true) String siteId,
            Model model,
            HttpServletRequest request) {
        try {
            RedirectResult result = this.routerService
                    .getRedirection(RedirectRequest.builder()
                            .alias(siteId)
                            .ip(ServletHelpers.getRequestIp(request))
                            .userAgent(ServletHelpers.getUserAgent(request))
                            .build());
            String destination = result.getDestination();
            logger.info("WebController.redirect: siteId = "
                    + siteId
                    + ", to = "
                    + destination);

            model.addAttribute("destinationUrl", destination);
            model.addAttribute("timestamp", result.getCreatedTimestamp());
            return "redirect";
        } catch (Exception e) {
            logger.warn("WebController.redirect: siteId = "
                    + siteId
                    + ", error = "
                    + e.getMessage());
            throw e;
        }

    }
}
