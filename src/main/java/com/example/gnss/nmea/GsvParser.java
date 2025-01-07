package com.example.gnss.nmea;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GsvParser {

    public static void parser(String lineData) {
        Pattern pattern = Pattern.compile("\\$(.*?)\\*");
        Matcher matcher = pattern.matcher(lineData);

        if (matcher.find()) {
            String data = matcher.group(1);
            String[] parts = data.split(",");
            String type = parts[0].substring(0,2);

            for(int i=0; i<(parts.length-4)/4; i++) {
                if(parts[i] != null && parts[i].length() > 0) {
                    int nsat = Integer.valueOf(parts[4+4*i]);
                    int elevation = Integer.valueOf(parts[4+4*i+1]);
                    int azimuth = Integer.valueOf(parts[4+4*i+2]);
                    int ratio = Integer.valueOf(parts[4+4*i+3]);

                    log.info("type:{} nsat: {} ele: {} azi:{} ratio: {}", type, nsat, elevation, azimuth, ratio);
                }
            }
        }
    }
}
