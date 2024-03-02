package org.ntranlab.url.models.statistics;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.*;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Measurement(name = "site_view")
public class SiteViewStats {
    @Column(tag = true, name = "site_id")
    private String siteId;

    @Column(tag = true, name = "destination")
    public String destination;

    @Column(name = "success")
    private Boolean success;

    @Column(timestamp = true, name = "timestamp")
    public Date timestamp;

    @Column(tag = true, name = "ip")
    private String ip;

    @Column(tag = true, name = "user_agent")
    private String userAgent;

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SiteViewBySuccess {
        @Column(tag = true, name = "site_id")
        private String siteId;

        @Column(tag = true, name = "destination")
        public String destination;

        @Column(name = "_value")
        private Boolean success;

        @Column(name = "_time")
        private Instant timestamp;

        @Column(tag = true, name = "ip")
        private String ip;

        @Column(tag = true, name = "user_agent")
        private String userAgent;
    }
}
