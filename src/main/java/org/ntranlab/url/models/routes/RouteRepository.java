package org.ntranlab.url.models.routes;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RouteRepository extends CrudRepository<Route, String> {
    Optional<Route> findByAlias(String alias);

    List<Route> findAllByIdIsIn(List<String> ids);

    @Query("SELECT r FROM Route r WHERE r.alias = :alias AND r.disabled = false")
    Optional<Route> findByAliasAndEnabled(
            @Param("alias") String alias);
}