package org.ntranlab.url.helpers.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Query {
    private int page;
    private int size;
    private String[] sort;

    public Pageable toPageable() {
        if (this.isEmpty()) {
            return Pageable.unpaged();
        }
        List<Sort.Order> orders = new ArrayList<>();
        if (this.sort != null) {
            for (String s : this.sort) {
                SortField.fromString(s)
                        .map(f -> switch (f.getDirection()) {
                            case ASC -> Sort.Order.asc(f.getName());
                            case DESC -> Sort.Order.desc(f.getName());
                        })
                        .ifPresent(orders::add);
            }
        }

        if (orders.isEmpty()) {
            if (this.isUnpaged()) {
                return Pageable.unpaged();
            }
            return PageRequest.of(
                    this.getPage(),
                    this.getSize(),
                    Sort.unsorted());

        }

        if (this.isUnpaged()) {
            return Pageable.unpaged(Sort.by(orders));
        }
        return PageRequest.of(
                this.getPage(),
                this.getSize(),
                Sort.by(orders));
    }

    public boolean isUnpaged() {
        return this.page == 0
                && this.size == 0;
    }

    public boolean isEmpty() {
        return this.isUnpaged()
                && (this.sort == null || this.sort.length == 0);
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class SortField {
        private String name;
        private Sort.Direction direction;

        public static Optional<SortField> fromString(String s) {
            if (s.isEmpty()) {
                return Optional.empty();
            }
            String[] splitted = s.split(":", 2);
            if (splitted.length == 0) {
                return Optional.empty();
            }
            String name = splitted[0];
            if (name == null || name.isEmpty()) {
                return Optional.empty();
            }
            if (splitted.length == 1) {
                return Optional.of(SortField.builder()
                        .direction(Sort.Direction.ASC)
                        .name(name)
                        .build());
            }
            String dir = splitted[1];
            if (dir == null || dir.isEmpty()) {
                return Optional.of(SortField.builder()
                        .direction(Sort.Direction.ASC)
                        .name(name)
                        .build());
            }
            Sort.Direction direction = Sort.Direction.fromString(dir);
            return Optional.of(SortField.builder()
                    .direction(direction)
                    .name(name)
                    .build());
        }
    }
}
