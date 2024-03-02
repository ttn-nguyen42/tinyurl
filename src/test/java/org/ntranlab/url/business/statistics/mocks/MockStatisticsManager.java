package org.ntranlab.url.business.statistics.mocks;

import org.ntranlab.url.business.statistics.StatisticsManager;
import org.ntranlab.url.models.redirect.RedirectRequest;
import org.ntranlab.url.models.redirect.RedirectResult;
import org.ntranlab.url.models.statistics.SiteViewStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MockStatisticsManager implements StatisticsManager {
    private final Logger logger = LoggerFactory.getLogger(MockStatisticsManager.class);

    @Override
    public void onSuccessfulRedirect(RedirectResult redirect, RedirectRequest request) {
        System.out.println(
                "MockStatisticsManager.onSuccessfulRedirect: redirect = " + redirect.toString() + ", request = "
                        + request.toString());
    }

    @Override
    public void onFailedRedirect(RedirectRequest request) {
        System.out.println("MockStatisticsManager.onFailedRedirect: request = " + request.toString());
    }

    @Override
    public List<SiteViewStats.SiteViewBySuccess> getSiteViewStats(Optional<String> siteId, Date start, Date stop,
                                                                  Optional<Boolean> success, Optional<String> ip) {
        logger.info("MockStatisticsManager.getSiteViewStats: siteId = " + siteId + ", start = " + start
                + ", stop = " + stop + ", success = " + success + ", ip = " + ip);
        List<SiteViewStats.SiteViewBySuccess> stats = new ArrayList<>();
        stats.add(SiteViewStats.SiteViewBySuccess.builder()
                .destination("https://www.google.com")
                .ip("192.168.10.2")
                .success(true)
                .siteId("to-google")
                .timestamp(Instant.now())
                .build());
        return stats;
    }

}
