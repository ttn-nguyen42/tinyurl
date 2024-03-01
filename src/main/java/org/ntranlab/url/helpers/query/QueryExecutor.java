package org.ntranlab.url.helpers.query;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@Builder
@AllArgsConstructor
public class QueryExecutor<T> {
    private Pageable pageable;

    /**
     * Neither paged or unsorted
     */
    @NonNull
    private Unpaged<T> onUnpaged;

    /**
     * Paged but is unsorted
     */
    @NonNull
    private Unsorted<T> onUnsorted;

    /**
     * Paged and sorted
     */
    @NonNull
    private Paged<T> onPaged;

    /**
     * Sorted but have no pages
     */
    @NonNull
    private Sorted<T> onSorted;

    public interface Unpaged<T> {
        T query();
    }

    public interface Paged<T> {
        T query(@NonNull Pageable pageable);
    }

    public interface Unsorted<T> {
        T query(@NonNull Pageable pageable);
    }

    public interface Sorted<T> {
        T query(@NonNull Sort sorted);
    }

    public T run() {
        if (this.pageable == null) {
            if (this.onUnpaged == null) {
                throw new IllegalStateException("query on unpaged is not defined");
            }
            return this.onUnpaged.query();
        }

        boolean isPaged = this.pageable.isPaged();
        boolean isSorted = this.pageable.getSort() != null
                && this.pageable.getSort().isSorted();

        if (isPaged && isSorted) {
            if (this.onPaged == null) {
                throw new IllegalStateException("query on paged is not defined");
            }
            if (this.onSorted == null) {
                throw new IllegalStateException("query on unsorted is not defined");
            }
            try {
                return this.onPaged.query(this.pageable);
            } catch (PropertyReferenceException e) {
                Pageable unsorted = PageRequest.of(
                        this.pageable.getPageNumber(),
                        this.pageable.getPageSize());
                return this.onUnsorted.query(unsorted);
            }
        }

        if (isPaged) {
            if (this.onUnsorted == null) {
                throw new IllegalStateException("query on paged is not defined");
            }
            return this.onUnsorted.query(this.pageable);
        }

        try {
            if (this.onSorted == null) {
                throw new IllegalStateException("query on sorted is not defined");
            }
            if (this.onUnpaged == null) {
                throw new IllegalStateException("query on unpaged is not defined");
            }
            return this.onSorted.query(this.pageable.getSort());
        } catch (PropertyReferenceException e) {
            return this.onUnpaged.query();
        }
    }
}
