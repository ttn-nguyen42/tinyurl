
package org.ntranlab.url.routes.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.ntranlab.url.business.statistics.StatisticsManager;
import org.ntranlab.url.helpers.exceptions.types.BadRequestException;
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
    private final SimpleDateFormat rfc3339Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    private final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    public StatisticsController(StatisticsManager statisticsManager) {
        this.statisticsManager = statisticsManager;
    }

    /**
     * Get the statistics of the redirections
     * 
     * @path /stats/redirections
     * @method GET
     * 
     * @return
     */
    @RequestMapping(value = "/stats/redirects", method = RequestMethod.GET)
    @ResponseBody
    public GetSiteViewStats getSiteViewStats(
            @RequestParam(name = "siteId", required = false) Optional<String> siteId,
            @RequestParam(name = "start", required = false) Optional<String> start,
            @RequestParam(name = "end", required = false) Optional<String> end,
            @RequestParam(name = "success", required = false) Optional<Boolean> success,
            @RequestParam(name = "ip", required = false) Optional<String> ip) {
        logger.info("StatisticsController.getSiteViewStats: siteId = "
                + siteId
                + ", start = "
                + start
                + ", end = "
                + end
                + ", success = "
                + success
                + ", ip = "
                + ip);
        try {
            Date startDate = start.map(s -> {
                try {
                    return this.rfc3339Formatter.parse(
                            s);
                } catch (ParseException e) {
                    throw new BadRequestException("Invalid start date format");
                }
            }).orElse(Date.from(
                    Instant.now()
                            .minus(12, ChronoUnit.HOURS)));

            Date endDate = end.map(e -> {
                try {
                    return this.rfc3339Formatter.parse(
                            e);
                } catch (ParseException e1) {
                    throw new BadRequestException("Invalid end date format");
                }
            }).orElse(Date.from(
                    Instant.now()));

            if (startDate.after(endDate))
                throw new BadRequestException("start date cannot be after end date");

            List<SiteViewStats> stats = this.statisticsManager.getSiteViewStats(
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

}
