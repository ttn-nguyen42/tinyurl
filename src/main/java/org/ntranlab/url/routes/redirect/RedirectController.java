package org.ntranlab.url.routes.redirect;

import org.ntranlab.url.business.routers.RouterService;
import org.ntranlab.url.models.redirect.RedirectRequest;
import org.ntranlab.url.models.redirect.RedirectResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class RedirectController {
    private final RouterService routerService;

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
        RedirectResult result = this.routerService.getRedirection(RedirectRequest.builder()
                .alias(siteId)
                .build());
        return new RedirectView(result.getDestination());
    }
}
