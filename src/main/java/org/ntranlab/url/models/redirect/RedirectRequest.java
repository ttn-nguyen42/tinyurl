package org.ntranlab.url.models.redirect;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RedirectRequest {
    private String alias;
}
