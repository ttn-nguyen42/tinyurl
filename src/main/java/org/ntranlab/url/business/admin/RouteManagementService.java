package org.ntranlab.url.business.admin;

import java.util.List;
import java.util.Optional;

import org.ntranlab.url.helpers.exceptions.types.BadRequestException;
import org.ntranlab.url.models.routes.PSRouteRepository;
import org.ntranlab.url.models.routes.Route;
import org.ntranlab.url.models.routes.RouteRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RouteManagementService {
    private final RouteRepository routeRepository;
    private final PSRouteRepository psRouteRepository;

    public RouteManagementService(final RouteRepository routeRepository, final PSRouteRepository psRouteRepository) {
        this.routeRepository = routeRepository;
        this.psRouteRepository = psRouteRepository;
    }

    /**
     * Return all routes available
     *
     * @param ids      Query by IDs
     * @param pageable Perform paging if requested
     * @return List of routes
     */
    public List<Route> getRoutes(List<String> ids, Pageable pageable) {
        if (ids.isEmpty() && pageable != null) {
            return this.psRouteRepository
                    .findAll(pageable)
                    .stream()
                    .toList();
        }
        return this.psRouteRepository.findAllByIdIsIn(
                ids,
                pageable)
                .stream()
                .toList();
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
        updatedRoute.setDisabled(true);
        this.routeRepository.save(updatedRoute);
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
    }

}
