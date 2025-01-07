package com.example.gnss.configuration;

import lombok.Data;

/**
 * 使用对象作为Map的Key，需要重写hashcode和equals方法
 * @Data注解会自动重写上述两个方法
 * 使用intellij模板生成
 */
@Data
public class PropsKey {

    private String name;
    private String host;
    private int port;

    public PropsKey(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }
}
