package com.example.gnss.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "gnss")
public class GnssConfiguration {

    private List<Rtcm3Properties> rtcm3;

    private List<NmeaProperties> nmea;
}
