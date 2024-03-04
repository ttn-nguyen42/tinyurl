package org.ntranlab.url.routes.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.ntranlab.url.business.admin.RouteManagementService;
import org.ntranlab.url.business.routers.RouterService;
import org.ntranlab.url.helpers.query.Query;
import org.ntranlab.url.models.routes.CreateRouteResponse;
import org.ntranlab.url.models.routes.Route;
import org.ntranlab.url.models.routes.RouteOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RouteManagerController {
        private final RouterService routerService;
        private final RouteManagementService routeManagementService;

        private final Logger logger = LoggerFactory.getLogger(RouterService.class);

        public RouteManagerController(final RouterService routerService,
                        final RouteManagementService routeManagementService) {
                this.routerService = routerService;
                this.routeManagementService = routeManagementService;
        }

        /**
         * Shorten a URL
         * 
         * @path /shorten
         * @method POST
         * @body RouteOptions Options to shorten a URL
         * @return CreateRouteResponse
         */
        @RequestMapping(value = "/routes", method = RequestMethod.POST)
        @ResponseBody
        public CreateRouteResponse shorten(@RequestBody RouteOptions options) {
                logger.info("RouteManagerController.shorten: " + options.toString());
                return this.routerService
                                .addRoute(options);
        }

        /**
         * Retrieve all routes
         * 
         * @path /routes
         * @method GET
         * @param query
         * @param ids
         * @return List<Route>
         */
        @RequestMapping(value = "/routes", method = RequestMethod.GET)
        @ResponseBody
        public List<Route> getRoutes(
                        Optional<Query> query,
                        @RequestParam(name = "id", required = false) Optional<List<String>> ids) {
                logger.info("RouteManagerController.getRoutes: " + query.toString() + " " + ids);
                List<String> providedIds = ids.orElse(new ArrayList<>());
                Pageable pageable = query.orElse(Query.builder()
                                .size(20)
                                .page(0)
                                .sort(new String[] { "id:asc" })
                                .build()).toPageable();
                System.out.println(pageable.toString());
                return this.routeManagementService
                                .getRoutes(providedIds, pageable);
        }

        /**
         * Enables a route, llowing it to be used publically
         * 
         * @param id ID of the route
         */
        @RequestMapping(value = "/routes/enable/{id}", method = RequestMethod.POST)
        @ResponseBody
        public void enableRoute(
                        @PathVariable(name = "id") String id) {
                logger.info("RouteManagerController.enableRoute: " + id);
                this.routeManagementService
                                .enableRoute(id);
        }

        /**
         * Disables a route, preventing it from being accessed
         * 
         * @param id ID of the route
         */
        @RequestMapping(value = "/routes/disable/{id}", method = RequestMethod.POST)
        @ResponseBody
        public void disableRoute(
                        @PathVariable(name = "id") String id) {
                logger.info("RouteManagerController.disableRoute: " + id);
                this.routeManagementService
                                .disableRoute(id);
        }

        /**
         * 
         * Deletes a route completely
         * 
         * @param id ID of the route
         */
        @RequestMapping(value = "/routes/{id}", method = RequestMethod.DELETE)
        @ResponseBody
        public void deleteRoute(@PathVariable(name = "id") String id) {
                logger.info("RouteManagerController.deleteRoute: " + id);
                this.routeManagementService
                                .deleteRoute(id);
        }
}
