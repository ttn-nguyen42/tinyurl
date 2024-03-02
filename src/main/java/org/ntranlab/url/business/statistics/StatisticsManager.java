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
     * @param redirect The result from getting retrieving the route
     * @param request  The request to redirect
     */
    void onSuccessfulRedirect(RedirectResult redirect, RedirectRequest request);

    /**
     * Records the statistics of the failed redirection
     * 
     * @param request The request to redirect
     */
    void onFailedRedirect(RedirectRequest request);

    /**
     * Retrieves the statistics recorded of redirections
     *
     * @param siteId Alias of the mapping
     * @param start Beginning date
     * @param stop End date
     * @param success Whether the request made was successful
     * @param ip IP of the user
     * @return List of SiteViewBySuccess
     */
    List<SiteViewStats.SiteViewBySuccess> getSiteViewStats(
            Optional<String> siteId,
            Date start,
            Date stop,
            Optional<Boolean> success,
            Optional<String> ip);

    /**
     * Retrieves the view count for each mapping
     *
     * @param siteId  Alias of the mapping
     * @param start   Beginning date
     * @param end     End date
     * @param success Whether the request made was successful
     * @return List of SiteViewConut
     */
    List<SiteViewStats.SiteViewCount> getSiteViewCount(
            Optional<String> siteId,
            Date start,
            Date end,
            Optional<Boolean> success);

}
