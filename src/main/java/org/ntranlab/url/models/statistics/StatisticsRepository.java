package org.ntranlab.url.models.statistics;

import com.influxdb.LogLevel;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteOptions;
import com.influxdb.client.domain.WritePrecision;
import io.reactivex.rxjava3.core.BackpressureOverflowStrategy;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.ntranlab.url.configs.InfluxDbConfigs;
import org.ntranlab.url.helpers.exceptions.types.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class StatisticsRepository {
    private Logger logger = LoggerFactory.getLogger(StatisticsRepository.class);

    private InfluxDBClient influxDb;
    private InfluxDbConfigs configs;
    private WriteApi writer;

    private SimpleDateFormat rfc3339Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    public StatisticsRepository(InfluxDbConfigs configs) {
        this.configs = configs;
    }

    /**
     * Runs after dependency injection
     */
    @PostConstruct
    public void init() {
        this.connect();
        this.testConnection();
        this.configure();
    }

    /**
     * Closes the batching and connections to InfluxDB
     */
    @PreDestroy
    public void destroy() {
        if (this.writer != null) {
            this.writer.close();
            this.logger.info("StatisticsRepository: closed InfluxDB writer");
        }
        if (this.influxDb != null) {
            this.influxDb.close();
            this.logger.info("StatisticsRepository: closed InfluxDB connection");
        }
    }

    /**
     * Connects to InfluxDB connection
     */
    private void connect() {
        if (configs.getUrl() == null) {
            throw new RuntimeException("InfluxDB URL is not set");
        }
        if (configs.getToken() == null) {
            throw new RuntimeException("InfluxDB token is not set");
        }
        this.influxDb = InfluxDBClientFactory.create(
                configs.getUrl(),
                configs.getToken()
                        .toCharArray());
    }

    /**
     * Test the connection to InfluxDB
     */
    private void testConnection() {
        boolean good = this.influxDb.ping();
        if (!good) {
            throw new RuntimeException("InfluxDB connection failed");
        }
    }

    /**
     * Configure additional InfluxDB client settings
     */
    private void configure() {
        this.influxDb.enableGzip();
        this.influxDb.setLogLevel(
                LogLevel.BASIC);
        this.writer = this.influxDb.makeWriteApi(
                WriteOptions.builder()
                        .batchSize(50)
                        .backpressureStrategy(BackpressureOverflowStrategy.DROP_OLDEST)
                        .flushInterval(10_000)
                        .build());
    }

    public void recordSiteView(SiteViewStats stats) {
        this.logger.debug("StatisticsRepository.recordSiteView: stats = " + stats.toString());
        this.writer.writeMeasurement(
                this.configs.getBucket(),
                this.configs.getOrganization(),
                WritePrecision.S,
                stats);
    }

    public List<SiteViewStats.SiteViewBySuccess> getSiteViewStats(
            Optional<String> siteId,
            Date start,
            Date stop,
            Optional<Boolean> success,
            Optional<String> ip) {
        String q = this.getSiteViewStatsQueryBuilder(
                siteId,
                start,
                stop,
                success,
                ip);
        logger.info("StatisticsRepository.getSiteViewStats: query = " + q);
        return this.influxDb.getQueryApi()
                .query(q, this.configs.getOrganization(), SiteViewStats.SiteViewBySuccess.class);
    }

    public List<SiteViewStats.SiteViewCount> getSiteViewCount(
            Optional<String> siteId,
            Date start,
            Date stop,
            Optional<Boolean> success) {
        String q = this.getSiteViewCountQueryBuilder(siteId, start, stop, success);
        logger.info("StatisticsRepository.getSiteViewStats: query = " + q);
        return this.influxDb.getQueryApi()
                .query(q, this.configs.getOrganization(), SiteViewStats.SiteViewCount.class);
    }


    private String getSiteViewStatsQueryBuilder(
            Optional<String> siteId,
            Date start,
            Date stop,
            Optional<Boolean> success,
            Optional<String> ip) {
        String base = this.getBaseQuery(start, stop);
        base += siteId.map(sid -> {
            if (!sid.isBlank()) {
                return " |> filter(fn: (r) => r[\"site_id\"] == \"" + sid + "\")";
            }
            return "";
        }).orElse("");
        base += success.map(s -> {
            String fieldSelector = " |> filter(fn: (r) => r[\"field\"] == \"success\"";
            if (s) {
                return fieldSelector + " |> filter(fn: (r) => r[\"_value\"] == true)";
            }
            return fieldSelector + " |> filter(fn: (r) => r[\"_value\"] == false)";
        }).orElse("");
        base += ip.map(i -> {
            if (!i.isBlank()) {
                return " |> filter(fn: (r) => r[\"ip\"] == \"" + i + "\")";
            }
            return "";
        }).orElse("");
        return base;
    }

    private String getBaseQuery(Date start, Date stop) {
        if (start == null || stop == null) {
            throw new BadRequestException("start and stop dates are required");
        }
        if (start.compareTo(stop) > 0) {
            throw new BadRequestException("start date must be before stop date");
        }
        String startStr = this.rfc3339Formatter.format(start);
        String endStr = this.rfc3339Formatter.format(stop);
        return "from(bucket: \"tinyurl\") |> range(start: " + startStr + ", stop: " + endStr + ") |> filter(fn: (r) => r[\"_measurement\"] == \"site_view\")";
    }

    private String getSiteViewCountQueryBuilder(Optional<String> siteId, Date start, Date stop, Optional<Boolean> success) {
        String base = this.getBaseQuery(start, stop);
        base += siteId.map(sid -> {
            if (!sid.isBlank()) {
                return " |> filter(fn: (r) => r[\"site_id\"] == \"" + sid + "\")";
            }
            return "";
        }).orElse("");
        base += success.map(s -> {
            String fieldSelector = " |> filter(fn: (r) => r[\"_field\"] == \"success\")";
            if (s) {
                return fieldSelector + " |> filter(fn: (r) => r[\"_value\"] == true)";
            }
            return fieldSelector + " |> filter(fn: (r) => r[\"_value\"] == false)";
        }).orElse("");
        base += " |> group(columns: [\"_measurement\", \"destination\", \"site_id\", \"_field\"]) |> count() |> yield(name: \"count\")";
        return base;
    }

}
