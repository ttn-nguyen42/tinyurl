package org.ntranlab.url.models.statistics;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetSiteViewStats {
    private List<SiteViewStats.SiteViewBySuccess> stats;

    private int size;
}
