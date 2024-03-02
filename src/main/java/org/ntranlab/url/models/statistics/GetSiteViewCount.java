package org.ntranlab.url.models.statistics;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class GetSiteViewCount {
    private int size;

    private List<SiteViewStats.SiteViewCount> stats;
}
