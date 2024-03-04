package org.ntranlab.url.business.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.ntranlab.url.helpers.exceptions.types.BadRequestException;
import org.ntranlab.url.helpers.query.QueryExecutor;
import org.ntranlab.url.models.routes.PSRouteRepository;
import org.ntranlab.url.models.routes.Route;
import org.ntranlab.url.models.routes.RouteRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RouteManagementService {
    private final RouteRepository routeRepository;
    private final PSRouteRepository psRouteRepository;

    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, Route> hashops;
    private static final String HASH_KEY = "ROUTES";

    public RouteManagementService(final RouteRepository routeRepository,
            final PSRouteRepository psRouteRepository,
            final RedisTemplate<String, Object> redisTemplate) {
        this.routeRepository = routeRepository;
        this.psRouteRepository = psRouteRepository;
        this.redisTemplate = redisTemplate;
        this.hashops = this.redisTemplate.opsForHash();
    }

    /**
     * Return all routes available
     *
     * @param ids      Query by IDs
     * @param pageable Perform paging if requested
     * @return List of routes
     */
    public List<Route> getRoutes(List<String> ids, Pageable pageable) {
        return QueryExecutor
                .<List<Route>>builder()
                .pageable(pageable)
                .onPaged(p -> {
                    if (ids.isEmpty()) {
                        return this.psRouteRepository.findAll(p)
                                .stream()
                                .toList();
                    }
                    return this.psRouteRepository.findAllByIdIsIn(ids, p)
                            .stream()
                            .toList();
                })
                .onSorted(s -> {
                    List<Route> routes = new ArrayList<>();
                    if (ids.isEmpty()) {
                        this.psRouteRepository.findAll(s)
                                .iterator()
                                .forEachRemaining(routes::add);
                        return routes;
                    }
                    this.psRouteRepository.findAllByIdIsIn(ids, s)
                            .iterator()
                            .forEachRemaining(routes::add);
                    return routes;
                })
                .onUnpaged(() -> {
                    if (ids.isEmpty()) {
                        List<Route> routes = new ArrayList<>();
                        this.routeRepository.findAll()
                                .iterator()
                                .forEachRemaining(routes::add);
                        return routes;
                    }
                    return this.routeRepository.findAllByIdIsIn(ids)
                            .stream()
                            .toList();
                })
                .onUnsorted(p -> {
                    if (ids.isEmpty()) {
                        return this.psRouteRepository.findAll(p)
                                .stream()
                                .toList();
                    }
                    return this.psRouteRepository.findAllByIdIsIn(ids, p)
                            .stream()
                            .toList();
                })
                .build()
                .run();
    }

    /**
     * Disable a route from being used
     * 
     * @param id ID of the route
     */
    public void disableRoute(String id) {
        if (id.isEmpty()) {
            throw new BadRequestException("id is required");
        }

        Optional<Route> route = this.routeRepository.findById(id);
        if (route.isEmpty()) {
            throw new BadRequestException("route not found");
        }

        Route updatedRoute = route.get();
        if (updatedRoute.isDisabled()) {
            return;
        }

        updatedRoute.setDisabled(true);

        this.routeRepository.save(updatedRoute);

        this.hashops.put(HASH_KEY, id, updatedRoute);
        this.hashops.put(HASH_KEY, updatedRoute.getAlias(), updatedRoute);
    }

    /**
     * Enable a route to be used
     * 
     * @param id ID of the route
     */
    public void enableRoute(String id) {
        if (id.isEmpty()) {
            throw new BadRequestException("id is required");
        }

        Optional<Route> route = this.routeRepository.findById(id);
        if (route.isEmpty()) {
            throw new BadRequestException("route not found");
        }

        Route updatedRoute = route.get();
        if (!updatedRoute.isDisabled()) {
            return;
        }
        updatedRoute.setDisabled(false);

        this.routeRepository.save(updatedRoute);

        this.hashops.put(HASH_KEY, id, updatedRoute);
        this.hashops.put(HASH_KEY, updatedRoute.getAlias(), updatedRoute);
    }

    /**
     * Delete a route from the database
     * 
     * @param id ID of the route
     */
    public void deleteRoute(String id) {
        if (id.isEmpty()) {
            throw new BadRequestException("id is required");
        }

        Optional<Route> route = this.routeRepository.findById(id);
        if (route.isEmpty()) {
            throw new BadRequestException("route not found");
        }

        this.routeRepository.deleteById(id);
        this.hashops.delete(HASH_KEY, route.get()
                .getId());
        this.hashops.delete(HASH_KEY, route.get()
                .getAlias());
    }

}
