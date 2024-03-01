package org.ntranlab.url.models.statistics;

import java.util.Date;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Measurement(name = "site_view")
public class SiteViewStats {
    @Column(tag = true)
    private String siteId;

    @Column(tag = true)
    public String destination;

    @Column(tag = true)
    private boolean success;

    @Column(timestamp = true)
    public Date timestamp;

    @Column
    private String ip;

    @Column
    private String userAgent;
}
