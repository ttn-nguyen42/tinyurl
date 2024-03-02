package org.ntranlab.url.business.statistics;

import org.ntranlab.url.models.redirect.RedirectRequest;
import org.ntranlab.url.models.redirect.RedirectResult;
import org.ntranlab.url.models.statistics.SiteViewStats;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface StatisticsManager {

    /**
     * Records the statistics of the redirection
     * 
     * @param redirect The result from getting retriving the route
     * @param request  The request to redirect
     */
    public void onSuccessfulRedirect(RedirectResult redirect, RedirectRequest request);

    /**
     * Records the statistics of the failed redirection
     * 
     * @param request The request to redirect
     */
    public void onFailedRedirect(RedirectRequest request);

    /**
     * Retrives the statistics recorded of redirections
     * 
     * @param siteId
     * @param start
     * @param stop
     * @param success
     * @param ip
     * @return
     */
    public List<SiteViewStats.SiteViewBySuccess> getSiteViewStats(
            Optional<String> siteId,
            Date start,
            Date stop,
            Optional<Boolean> success,
            Optional<String> ip);
}
