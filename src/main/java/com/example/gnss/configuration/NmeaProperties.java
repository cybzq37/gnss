package com.example.gnss.configuration;

import lombok.Data;

@Data
public class NmeaProperties {

    private String name;
    private String host;
    private int port;
}
