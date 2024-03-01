package org.ntranlab.url.helpers.query;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Optional;

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
        Sort sort = Sort.by(new ArrayList<>());
        if (this.sort != null) {
            for (String s : this.sort) {
                SortField.fromString(s)
                        .map(f -> Sort.by(f.getDirection(), f.getName()))
                        .ifPresent(sort::and);
            }
        }

        if (this.isUnpaged()) {
            return Pageable.unpaged(sort);
        }

        return PageRequest.of(
                this.getPage(),
                this.getSize(),
                sort);
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
            String[] splitted = s.split(",", 2);
            if (splitted.length == 0) {
                return Optional.empty();
            }
            if (splitted.length == 1) {
                return Optional.of(SortField.builder()
                        .direction(Sort.Direction.ASC)
                        .name(splitted[0])
                        .build());
            }
            String dir = splitted[1];
            Sort.Direction direction = Sort.Direction.fromString(dir);
            return Optional.of(SortField.builder()
                    .direction(direction)
                    .name(splitted[0])
                    .build());
        }
    }
}
