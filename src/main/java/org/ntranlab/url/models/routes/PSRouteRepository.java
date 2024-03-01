package org.ntranlab.url.models.routes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PSRouteRepository extends PagingAndSortingRepository<Route, String> {
    Page<Route> findAllByIdIsIn(List<String> ids, Pageable pageable);
}
