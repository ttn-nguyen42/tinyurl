package org.ntranlab.url.business.statistics;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.ntranlab.url.models.redirect.RedirectRequest;
import org.ntranlab.url.models.redirect.RedirectResult;
import org.ntranlab.url.models.statistics.SiteViewStats;
import org.ntranlab.url.models.statistics.StatisticsRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

@Service
public class StatisticsManager {
    private StatisticsRepository sRepository;
    private ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public StatisticsManager(StatisticsRepository sRepository) {
        this.sRepository = sRepository;
    }

    /**
     * Records the statistics of the redirection
     * 
     * @param redirect The result from getting retriving the route
     * @param request  The request to redirect
     */
    public void onSuccessfulRedirect(RedirectResult redirect, RedirectRequest request) {
        SiteViewStats stats = SiteViewStats.builder()
                .destination(redirect.getDestination())
                .ip(request.getIp())
                .siteId(request.getAlias())
                .userAgent(request.getUserAgent())
                .timestamp(request.getTimestamp())
                .success(true)
                .build();
        executor.submit(() -> {
            this.sRepository.recordSiteView(stats);
        });
    }

    /**
     * Records the statistics of the failed redirection
     * 
     * @param request The request to redirect
     */
    public void onFailedRedirect(RedirectRequest request) {
        SiteViewStats stats = SiteViewStats.builder()
                .destination("")
                .ip(request.getIp())
                .siteId(request.getAlias())
                .userAgent(request.getUserAgent())
                .timestamp(request.getTimestamp())
                .success(false)
                .build();
        executor.submit(() -> {
            this.sRepository.recordSiteView(stats);
        });
    }

    @PreDestroy
    public void shutdown() {
        if (executor != null) {
            executor.shutdown();
            try {
                boolean preTerminationDone = executor.awaitTermination(10, TimeUnit.SECONDS);
                if (preTerminationDone) {
                    return;
                }
                executor.shutdownNow();
                boolean strictTerminationDone = executor.awaitTermination(10, TimeUnit.SECONDS);
                if (!strictTerminationDone) {
                    throw new IllegalStateException("StatisticsManager: failed to shutdown executor");
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }
}
