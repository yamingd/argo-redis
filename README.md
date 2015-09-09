# argo-redis
redis template with spring, redis

# Usage
1. place config file (redis.yaml) at classPath
```
maxActive: 100
maxIdle: 100
timeout: 5000
host: 127.0.0.1
port: 6379
testOnBorrow: true
testWhileIdle: true

```

2. Inject RedisBucket
```
@Autowired
RedisBuket redisBuket;

```

3. you will get it
