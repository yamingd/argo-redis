package com.argo.redis;

import com.argo.yaml.YamlTemplate;
import com.google.common.base.MoreObjects;

import java.io.IOException;

public class RedisConfig {

    private static final String confName = "redis.yaml";

    public static RedisConfig instance = null;

    /**
     * 加载配置信息
     * @throws IOException
     */
    public synchronized static void load() throws IOException {
        if (instance != null){
            return;
        }
        RedisConfig.instance = YamlTemplate.load(RedisConfig.class, confName);
    }

    private Integer maxActive;
    private Integer maxIdle;
    private Integer timeout;
    private String host;
    private Integer port;
    private Boolean testOnBorrow = true;
    private Boolean testWhileIdle = true;

    public Integer getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public Boolean getTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(Boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("maxActive", maxActive)
                .add("maxIdle", maxIdle)
                .add("timeout", timeout)
                .add("host", host)
                .add("port", port)
                .add("testOnBorrow", testOnBorrow)
                .add("testWhileIdle", testWhileIdle)
                .toString();
    }
}
