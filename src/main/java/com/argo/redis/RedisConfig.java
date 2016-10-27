package com.argo.redis;

import com.argo.yaml.YamlTemplate;

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

    public static class Cluster{
        public boolean enabled = false;
        public List<String> hosts = null;

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("Cluster{");
            sb.append("enabled=").append(enabled);
            sb.append(", hosts=").append(hosts);
            sb.append('}');
            return sb.toString();
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
    private Integer timeout = 2000;
    private String host;
    private Integer port;
    private Boolean testOnBorrow = true;
    private Boolean testWhileIdle = true;
    private Sentinel sentinel;
    private Cluster cluster;
    private Integer aliveCheck = 300; // 30s
    private String passwd;

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

    public Integer getAliveCheck() {
        return aliveCheck;
    }

    public void setAliveCheck(Integer aliveCheck) {
        this.aliveCheck = aliveCheck;
    }


    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RedisConfig{");
        sb.append("maxActive=").append(maxActive);
        sb.append(", maxIdle=").append(maxIdle);
        sb.append(", timeout=").append(timeout);
        sb.append(", host='").append(host).append('\'');
        sb.append(", port=").append(port);
        sb.append(", testOnBorrow=").append(testOnBorrow);
        sb.append(", testWhileIdle=").append(testWhileIdle);
        sb.append(", sentinel=").append(sentinel);
        sb.append(", cluster=").append(cluster);
        sb.append(", aliveCheck=").append(aliveCheck);
        sb.append('}');
        return sb.toString();
    }
}
