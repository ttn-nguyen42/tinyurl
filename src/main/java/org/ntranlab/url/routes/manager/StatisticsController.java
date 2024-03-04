
package org.ntranlab.url.routes.manager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.ntranlab.url.business.statistics.StatisticsManager;
import org.ntranlab.url.helpers.exceptions.types.BadRequestException;
import org.ntranlab.url.helpers.query.DurationConverter;
import org.ntranlab.url.models.statistics.GetSiteViewCount;
import org.ntranlab.url.models.statistics.GetSiteViewStats;
import org.ntranlab.url.models.statistics.SiteViewStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {
    private final StatisticsManager statisticsManager;

    private final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    public StatisticsController(StatisticsManager statisticsManager) {
        this.statisticsManager = statisticsManager;
    }

    /**
     * Get the statistics of the redirections
     * 
     * @path /stats/redirections
     * @method GET
     * @param siteId  Alias or site ID of the mapping to include
     * @param since   Data since
     * @param success Filters out success or failed attempts
     * @param ip      Filters out IP
     * @return List of SiteViewStats
     */
    @RequestMapping(value = "/api/stats/redirects", method = RequestMethod.GET)
    @ResponseBody
    public GetSiteViewStats getSiteViewStats(
            @RequestParam(name = "siteId", required = false) Optional<String> siteId,
            @RequestParam(name = "since", required = false) Optional<String> since,
            @RequestParam(name = "success", required = false) Optional<Boolean> success,
            @RequestParam(name = "ip", required = false) Optional<String> ip) {
        logger.info("StatisticsController.getSiteViewStats: siteId = "
                + siteId
                + ", since = "
                + since + ", success = "
                + success
                + ", ip = "
                + ip);
        try {
            Date startDate = since.map(s -> {
                try {
                    DurationConverter converter = new DurationConverter();
                    return converter.convert(s);
                } catch (BadRequestException e) {
                    throw new BadRequestException("Invalid time range format: " + e.getMessage());
                }
            }).orElse(Date.from(
                    Instant.now()
                            .minus(12, ChronoUnit.HOURS)));

            Date endDate = (Date.from(
                    Instant.now()));

            if (startDate.after(endDate))
                throw new BadRequestException("start date cannot be after end date");

            List<SiteViewStats.SiteViewBySuccess> stats = this.statisticsManager.getSiteViewStats(
                    siteId,
                    startDate,
                    endDate,
                    success,
                    ip);
            logger.info("StatisticsController.getSiteViewStats: stats = "
                    + stats.size());
            return GetSiteViewStats.builder()
                    .stats(stats)
                    .size(stats.size())
                    .build();
        } catch (Exception e) {
            logger.warn("StatisticsController.getSiteViewStats: error = "
                    + e.getMessage());
            throw e;
        }

    }

    /**
     * Get the view count of each mapping pairs
     *
     * @path /stats/counts
     * @method GET
     * @param siteId  Alias or site ID of the mapping
     * @param since   Data since
     * @param success Filters out success or failed attempts
     * @return List of SiteViewCount
     */
    @RequestMapping(value = "/api/stats/counts", method = RequestMethod.GET)
    @ResponseBody
    public GetSiteViewCount getSiteViewCount(
            @RequestParam(name = "siteId", required = false) Optional<String> siteId,
            @RequestParam(name = "since", required = false) Optional<String> since,
            @RequestParam(name = "success", required = false) Optional<Boolean> success) {
        logger.info("StatisticsController.getSiteViewCount: siteId = " + siteId
                + ", since = " + since
                + ", success = " + success);

        try {
            Date startDate = since.map(s -> {
                try {
                    DurationConverter converter = new DurationConverter();
                    return converter.convert(s);
                } catch (BadRequestException e) {
                    throw new BadRequestException("Invalid time range format: " + e.getMessage());
                }
            }).orElse(Date.from(
                    Instant.now()
                            .minus(12, ChronoUnit.HOURS)));

            Date endDate = (Date.from(
                    Instant.now()));

            if (startDate.after(endDate))
                throw new BadRequestException("start date cannot be after end date");

            List<SiteViewStats.SiteViewCount> stats = this.statisticsManager.getSiteViewCount(
                    siteId,
                    startDate,
                    endDate,
                    success);
            logger.info("StatisticsController.getSiteViewCount: stats = "
                    + stats.size());
            return GetSiteViewCount.builder()
                    .stats(stats)
                    .size(stats.size())
                    .build();
        } catch (Exception e) {
            logger.warn("StatisticsController.getSiteViewCount: error = "
                    + e.getMessage());
            throw e;
        }
    }
}
