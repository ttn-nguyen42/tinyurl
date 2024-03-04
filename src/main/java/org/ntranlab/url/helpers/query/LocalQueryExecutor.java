package org.ntranlab.url.helpers.query;

import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Pageable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@Builder
@AllArgsConstructor
public class LocalQueryExecutor<T> {
    private Pageable pageable;
    private Comparator<T> comparator;

    @NonNull
    private Query<T> query;

    public List<T> run() {
        List<T> all = this.query.query();
        if (all.isEmpty()) {
            return all;
        }
        return makeExecutor(all).run();
    }

    public QueryExecutor<List<T>> makeExecutor(List<T> all) {
        return QueryExecutor.<List<T>>builder()
                .pageable(pageable)
                .onPaged(p -> {
                    if (this.comparator == null) {
                        throw new IllegalStateException("comparator is not defined");
                    }
                    all.sort(comparator);
                    long skipped = p.getOffset();
                    long end = skipped + p.getPageSize();
                    if (end > all.size()) {
                        end = all.size();
                    }
                    return all.subList((int) skipped, (int) end);
                })
                .onUnsorted(p -> {
                    long skipped = p.getOffset();
                    long end = skipped + p.getPageSize();
                    if (end > all.size()) {
                        end = all.size();
                    }
                    return all.subList((int) skipped, (int) end);
                })
                .onUnpaged(() -> {
                    return all;
                })
                .onSorted(s -> {
                    if (this.comparator == null) {
                        throw new IllegalStateException("comparator is not defined");
                    }
                    all.sort(comparator);
                    return all;
                })
                .build();
    }

    public interface Query<I> {
        List<I> query();
    }
}
