package org.ntranlab.url.models.routes;

import java.io.Serializable;
import java.sql.Date;
import java.util.Comparator;

import org.springframework.lang.NonNull;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Route implements Serializable {
    @Id
    private String id;

    @NonNull
    private String alias;

    @NonNull
    private String destination;

    private boolean disabled;
    private Date timestamp;

    public static Comparator<Route> getComparator(String... fields) {
        if (fields.length == 0) {
            return null;
        }
        Comparator<Route> comparator = null;
        for (String f : fields) {
            switch (f) {
                case "id":
                    if (comparator == null) {
                        comparator = Comparator.comparing(Route::getId);
                    } else {
                        comparator = comparator.thenComparing(Comparator.comparing(Route::getId));
                    }
                    break;
                case "alias":
                    if (comparator == null) {
                        comparator = Comparator.comparing(Route::getAlias);
                    } else {
                        comparator = comparator.thenComparing(Comparator.comparing(Route::getAlias));
                    }
                    break;
                case "destination":
                    if (comparator == null) {
                        comparator = Comparator.comparing(Route::getDestination);
                    } else {
                        comparator = comparator.thenComparing(Comparator.comparing(Route::getDestination));
                    }
                    break;
                case "disabled":
                    if (comparator == null) {
                        comparator = Comparator.comparing(Route::isDisabled);
                    } else {
                        comparator = comparator.thenComparing(Comparator.comparing(Route::isDisabled));
                    }
                    break;
                case "timestamp":
                    if (comparator == null) {
                        comparator = Comparator.comparing(Route::getTimestamp);
                    } else {
                        comparator = comparator.thenComparing(Comparator.comparing(Route::getTimestamp));
                    }
                    break;
                default:
                    throw new IllegalArgumentException("unknown field: " + f);
            }
        }
        return comparator;
    }

}
