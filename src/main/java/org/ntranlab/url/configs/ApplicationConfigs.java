package org.ntranlab.url.configs;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Component
public class ApplicationConfigs {
    private InfluxDbConfigs influx;

    public ApplicationConfigs(InfluxDbConfigs influx) {
        this.influx = influx;
    }

}
