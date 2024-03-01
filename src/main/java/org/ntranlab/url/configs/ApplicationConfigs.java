package org.ntranlab.url.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Component
public class ApplicationConfigs {
    private InfluxDbConfigs influx;

    private ApplicationConfigs(InfluxDbConfigs influx) {
        this.influx = influx;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @Component
    public class InfluxDbConfigs {
        @Value("${influxdb.url}")
        private String url;

        @Value("${influxdb.token}")
        private String token;

        @Value("${influxdb.organization}")
        private String organization;

        @Value("${influxdb.bucket}")
        private String bucket;
    }
}
