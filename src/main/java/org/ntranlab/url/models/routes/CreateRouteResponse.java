package org.ntranlab.url.models.routes;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateRouteResponse {
    private String shortenUrl;
}
