package org.ntranlab.url.business.routers;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ntranlab.url.business.statistics.StatisticsManager;
import org.ntranlab.url.helpers.exceptions.types.AlreadyExistsException;
import org.ntranlab.url.helpers.exceptions.types.BadRequestException;
import org.ntranlab.url.helpers.exceptions.types.NotFoundException;
import org.ntranlab.url.helpers.utils.ValidationHelpers;
import org.ntranlab.url.models.redirect.RedirectRequest;
import org.ntranlab.url.models.redirect.RedirectResult;
import org.ntranlab.url.models.routes.CreateRouteResponse;
import org.ntranlab.url.models.routes.Route;
import org.ntranlab.url.models.routes.RouteOptions;
import org.ntranlab.url.models.routes.RouteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RouterService {
    private final RouteRepository routeRepository;
    private final ValidationHelpers validator;
    private final StatisticsManager statisticsManager;

    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, Route> hashops;

    private static final String HASH_KEY_BY_ID = "ROUTES_BY_ID";
    private static final String HASH_KEY_BY_ALIAS = "ROUTES_BY_ALIAS";

    private final Logger logger = LoggerFactory.getLogger(RouterService.class);
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public RouterService(
            final RouteRepository routeRepository,
            ValidationHelpers validator,
            StatisticsManager statisticsManager,
            final RedisTemplate<String, Object> redisTemplate) {
        this.routeRepository = routeRepository;
        this.validator = validator;
        this.statisticsManager = statisticsManager;
        this.redisTemplate = redisTemplate;
        this.hashops = this.redisTemplate.opsForHash();
    }

    /**
     * Validates an alias mapped to destination URL and record it in the database
     *
     * @param options Options ot create the mapping
     * @return CreateRouteResponse Necessary settings and shortened URL
     */
    public CreateRouteResponse addRoute(RouteOptions options) {
        Route model = Route.builder()
                .alias(options.getAlias())
                .destination(options.getDestination())
                .id(UUID.randomUUID().toString())
                .timestamp(Date.valueOf(LocalDate.now()))
                .build();
        this.validateRoute(model);

        Optional<Route> existingRoute = this.routeRepository
                .findByAliasAndEnabled(model.getAlias());
        if (existingRoute.isPresent()) {
            throw new AlreadyExistsException("alias is already taken");
        }

        this.routeRepository.save(model);

        this.executor.submit(() -> {
            this.hashops.put(HASH_KEY_BY_ID, model.getId(), model);
            this.hashops.put(HASH_KEY_BY_ALIAS, model.getAlias(), model);
            this.logger.info("RouterService.addRoute: updated cache for route alias = " + model.getAlias());
        });

        String shortenUrl = "https://s.ntranlab.com/to/" +
                model.getAlias();

        return CreateRouteResponse.builder()
                .shortenUrl(shortenUrl)
                .build();
    }

    /**
     * Retrieves the associated mapping of alias to destination URL
     *
     * @param request Necessary information and request options
     * @return RedirectResult Necessary settings and destination URL associated to
     *         the alias
     */
    public RedirectResult getRedirection(RedirectRequest request) {
        this.validateRedirectRequest(request);

        Route route = this.hashops.get(HASH_KEY_BY_ALIAS, request.getAlias());
        if (route != null) {
            this.logger.info("RouterService.getRedirection: cache hit for route alias = " + request.getAlias());
            RedirectResult result = RedirectResult.builder()
                    .destination(route.getDestination())
                    .createdTimestamp(route.getTimestamp())
                    .build();
            this.statisticsManager.onSuccessfulRedirect(result, request);
            return result;

        }
        Optional<Route> existingRoute = this.routeRepository.findByAlias(request.getAlias());
        if (existingRoute.isEmpty()) {
            this.statisticsManager.onFailedRedirect(request);
            throw new NotFoundException("no existing route exists");
        }

        this.logger.info("RouterService.getRedirection: database hit for route alias = " + request.getAlias());
        final Route r = existingRoute.get();

        this.executor.submit(() -> {
            this.hashops.put(HASH_KEY_BY_ID, r.getId(), r);
            this.hashops.put(HASH_KEY_BY_ALIAS, r.getAlias(), r);
        });

        this.logger.info("RouterService.getRedirection: route = " + r.toString());

        if (r.isDisabled()) {
            this.statisticsManager.onFailedRedirect(request);
            throw new NotFoundException("route is disabled");
        }

        RedirectResult result = RedirectResult.builder()
                .destination(r.getDestination())
                .createdTimestamp(r.getTimestamp())
                .build();
        this.statisticsManager.onSuccessfulRedirect(result, request);
        return result;
    }

    private void validateRedirectRequest(RedirectRequest request) {
        if (request.getAlias().isEmpty()) {
            throw new NotFoundException("route is not found");
        }
        if (request.getAlias().length() < 5) {
            throw new NotFoundException("route is not found");
        }
        if (!this.validator.isValidAlias(request.getAlias())) {
            throw new NotFoundException("route is not found");
        }
    }

    private void validateRoute(Route route) {
        if (route.getAlias().isEmpty()) {
            throw new BadRequestException("route alias is empty");
        }
        if (route.getDestination().isEmpty()) {
            throw new BadRequestException("route destination is empty");
        }
        if (!this.validator.isValidUrl(route.getDestination())) {
            throw new BadRequestException("route destination is not a valid URL");
        }
        if (!this.validator.isValidAlias(route.getAlias())) {
            throw new BadRequestException("route alias is not alphanumeric");
        }
        if (route.getAlias().length() < 5) {
            throw new BadRequestException("route alias must be longer than 5 characters");
        }
    }
}