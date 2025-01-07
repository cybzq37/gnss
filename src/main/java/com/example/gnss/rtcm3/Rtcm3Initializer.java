package com.example.gnss.rtcm3;

import com.example.gnss.configuration.GnssConfiguration;
import com.example.gnss.configuration.PropsKey;
import com.example.gnss.configuration.Rtcm3Properties;
import com.example.gnss.rtcm3.netty.Rtcm3NettyTcpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = "com.navinfo.gnss.rtcm3")
@ConditionalOnExpression("'${gnss.rtcm3}'.length() > 0")
public class Rtcm3Initializer {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public Map<PropsKey, Rtcm3NettyTcpClient> rtcm3NettyTcpClients(GnssConfiguration rtcm3Configuration){
        Map<PropsKey, Rtcm3NettyTcpClient> rtcm3NettyTcpClients = new HashMap<>();
        for(Rtcm3Properties properties: rtcm3Configuration.getRtcm3()){
            PropsKey key = new PropsKey(properties.getName(), properties.getHost(), properties.getPort());
            Rtcm3NettyTcpClient rtcm3NettyTcpClient = new Rtcm3NettyTcpClient(properties);
            rtcm3NettyTcpClients.put(key, rtcm3NettyTcpClient);
        }
        return rtcm3NettyTcpClients;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().equals(applicationContext)) {
            Map<PropsKey, Rtcm3NettyTcpClient> rtcm3NettyTcpClients = applicationContext.getBean("rtcm3NettyTcpClients", Map.class);
            for (PropsKey key : rtcm3NettyTcpClients.keySet()) {
                rtcm3NettyTcpClients.get(key).init();
                rtcm3NettyTcpClients.get(key).connect();
            }
        }
    }
}
