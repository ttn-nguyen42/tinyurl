package org.ntranlab.url.models.routes;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RouteRepository extends CrudRepository<Route, String> {
    Optional<Route> findByAlias(String alias);

    @Query("SELECT r FROM Route r WHERE r.alias = :alias AND r.enabled = true")
    Optional<Route> findEnabledByAlias(
            @Param("alias") String alias);
}