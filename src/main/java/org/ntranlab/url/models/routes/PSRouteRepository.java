package org.ntranlab.url.models.routes;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PSRouteRepository extends PagingAndSortingRepository<Route, String> {
    Page<Route> findAllByIdIsIn(List<String> ids, Pageable pageable);

    Iterable<Route> findAllByIdIsIn(List<String> ids, Sort sort);
}
