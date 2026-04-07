package config;

import redis.clients.jedis.Jedis;

public class RedisConnection {
    private static final String HOST = "localhost";
    private static final int PORT = 6379;

    public static Jedis getConnection() {
        return new Jedis(HOST, PORT);
    }
}