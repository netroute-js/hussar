package pl.netroute.hussar.service.nosql.redis.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RedisClusterSettings {
    static final String REDIS_CLUSTER_IP_ENV = "IP";
    static final String REDIS_CLUSTER_MASTERS_ENV = "MASTERS";
    static final String REDIS_CLUSTER_SLAVES_PER_MASTER_ENV = "SLAVES_PER_MASTER";

    static final int REDIS_CLUSTER_MASTERS = 3;
    static final int REDIS_CLUSTER_SLAVES_PER_MASTER = 1;
    static final int REDIS_CLUSTER_NODES = REDIS_CLUSTER_MASTERS * (REDIS_CLUSTER_SLAVES_PER_MASTER + 1);

    static final String REDIS_CLUSTER_IP = "0.0.0.0";
    static final String REDIS_CLUSTER_LOOP_BACK_IP = "127.0.0.1";

    static final int REDIS_CLUSTER_FIRST_PORT = 7000;

    static final String REDIS_CLUSTER_USERNAME = "default";
    static final String REDIS_CLUSTER_PASSWORD = "test";
}
