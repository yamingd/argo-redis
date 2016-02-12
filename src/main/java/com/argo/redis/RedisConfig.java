package com.argo.redis;

import com.argo.yaml.YamlTemplate;
import com.google.common.base.MoreObjects;

import java.io.IOException;
import java.util.List;

public class RedisConfig {

    private static final String defaultConfName = "redis.yaml";

    public static RedisConfig instance = null;

    public static class Sentinel{
        public boolean enabled = false;
        public String master = null;
        public List<String> hosts = null;

        @Override
        public String toString() {
            return "Sentinel{" +
                    "enabled=" + enabled +
                    ", master='" + master + '\'' +
                    ", hosts=" + hosts +
                    '}';
        }
    }

    /**
     *
     * @throws IOException
     */
    public synchronized static void load() throws IOException {
        load(defaultConfName);
    }
    /**
     * 加载配置信息
     * @throws IOException
     */
    public synchronized static void load(String confName) throws IOException {
        if (instance != null){
            return;
        }
        if (null == confName){
            confName = defaultConfName;
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
    private Sentinel sentinel;

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

    public Sentinel getSentinel() {
        return sentinel;
    }

    public void setSentinel(Sentinel sentinel) {
        this.sentinel = sentinel;
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
                .add("sentinel", sentinel)
                .toString();
    }
}
