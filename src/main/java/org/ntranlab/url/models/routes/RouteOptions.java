package org.ntranlab.url.models.routes;

import org.springframework.lang.NonNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class RouteOptions {
    @NonNull
    private String alias;

    @NonNull
    private String destination;
}
