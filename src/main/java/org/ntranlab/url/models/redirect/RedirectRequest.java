package org.ntranlab.url.models.redirect;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RedirectRequest {
    private String alias;
    private String ip;
    private String userAgent;
    private Date timestamp;
}
