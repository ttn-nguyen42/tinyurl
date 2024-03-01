package org.ntranlab.url.routes.redirect;

import org.ntranlab.url.business.routers.RouterService;
import org.ntranlab.url.models.redirect.RedirectRequest;
import org.ntranlab.url.models.redirect.RedirectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class RedirectController {
    private final RouterService routerService;

    private Logger logger = LoggerFactory.getLogger(RedirectController.class);

    public RedirectController(RouterService routerService) {
        this.routerService = routerService;
    }

    /**
     * Redirects traffic from siteId to the actual site
     *
     * @param siteId Each shorten site is identified by siteId
     * @return RequestMapping
     */
    @RequestMapping(value = "/to/{siteId}", method = RequestMethod.GET)
    public RedirectView redirect(@PathVariable("siteId") String siteId) {
        try {
            RedirectResult result = this.routerService.getRedirection(RedirectRequest.builder()
                    .alias(siteId)
                    .build());
            String destination = result.getDestination();
            logger.info("RedirectController.redirect: siteId = "
                    + siteId
                    + ", to = "
                    + destination);

            return new RedirectView(destination);
        } catch (Exception e) {
            logger.warn("RedirectController.redirect: siteId = "
                    + siteId
                    + ", error = "
                    + e.getMessage());
            throw e;
        }

    }
}
