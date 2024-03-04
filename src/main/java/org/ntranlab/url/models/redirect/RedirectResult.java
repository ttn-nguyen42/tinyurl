package org.ntranlab.url.models.redirect;

import java.util.Date;

import org.springframework.lang.NonNull;

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
@ToString
@Builder
public class RedirectResult {
    @NonNull
    String destination;

    @NonNull
    Date createdTimestamp;
}
