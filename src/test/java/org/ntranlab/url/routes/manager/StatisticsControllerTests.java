package org.ntranlab.url.routes.manager;

import org.junit.jupiter.api.Test;
import org.ntranlab.url.business.statistics.StatisticsManager;
import org.ntranlab.url.business.statistics.mocks.MockStatisticsManager;
import org.ntranlab.url.models.statistics.GetSiteViewStats;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Optional;

@SpringBootTest
public class StatisticsControllerTests {

    @Test
    public void testGetSiteViewsStats_WithoutParameters() {
        StatisticsManager manager = new MockStatisticsManager();

        StatisticsController controller = new StatisticsController(manager);

        GetSiteViewStats result = controller.getSiteViewStats(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        Assert.notNull(result.getStats(), "Stats should not be null");
        Assert.notEmpty(result.getStats(), "Stats should not be empty");
    }

    @Test
    public void testGetSiteViewsStats_WithStartEnd() {
        StatisticsManager manager = new MockStatisticsManager();

        StatisticsController controller = new StatisticsController(manager);

        GetSiteViewStats result = controller.getSiteViewStats(
                Optional.empty(),
                Optional.of("2m"),
                Optional.empty(),
                Optional.empty());

        Assert.notNull(result.getStats(), "Stats should not be null");
        Assert.notEmpty(result.getStats(), "Stats should not be empty");
    }

}
