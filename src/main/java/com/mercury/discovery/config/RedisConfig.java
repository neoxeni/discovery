package com.mercury.discovery.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

/**
 * [스프링 데이터 Redis 만료 키](https://www.it-swarm.dev/ko/java/%EC%8A%A4%ED%94%84%EB%A7%81-%EB%8D%B0%EC%9D%B4%ED%84%B0-redis-%EB%A7%8C%EB%A3%8C-%ED%82%A4/822680783/)
 * [Spring Cache 장애 대응 방안](https://supawer0728.github.io/2018/04/18/spring-cache-fallback/)
 * [Cache SpEL](https://devidea.tistory.com/38)
 * [Spring Cache Tutorial](https://www.baeldung.com/spring-cache-tutorial)
 * [Spring 캐시 추상화](https://blog.outsider.ne.kr/1094)
 *
 *
 *
 *
 * spring:
 *     redis:
 *
 *         cluster: #[참고](https://mycup.tistory.com/285)
 *             nodes:
 *                 - 192.168.42.231:7001
 *                 - 192.168.42.231:7000
 *
 *
 *         sentinel: #[참고](https://ichi.pro/ko/redis-sentinel-mich-spring-lettuce-keullaieonteuleul-tonghan-go-gayongseong-173327786572930)
 *             master: mymaster
 *             nodes:
 *                 - 192.168.42.231:7002
 *                 - 192.168.42.231:7000
 *                 - 192.168.42.231:7001
 */

@RequiredArgsConstructor
@Configuration
@Slf4j
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        String host = redisProperties.getHost();
        int port = redisProperties.getPort();
        String password = redisProperties.getPassword();

        LettuceClientConfiguration lettuceClientConfiguration =
                LettucePoolingClientConfiguration.builder().poolConfig(createPoolConfig())
                        .commandTimeout(Duration.ofSeconds(5)).build();

        RedisProperties.Cluster cluster = redisProperties.getCluster();
        if(cluster != null){
            log.info("RedisConnectionFactory cluster mode. nodes:{}", cluster.getNodes());
            RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
            cluster.getNodes().forEach(s -> {
                String[] url = s.split(":");
                redisClusterConfiguration.clusterNode(url[0],Integer.parseInt(url[1]));
            });

            if(password != null && !"".equals(password)){
                redisClusterConfiguration.setPassword(password);
            }

            return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
        }

        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
        if(sentinel != null){
            log.info("RedisConnectionFactory sentinel mode. master:{}, nodes:{}", sentinel.getMaster(), sentinel.getNodes());
            RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration().master(sentinel.getMaster());
            sentinel.getNodes().forEach(s -> {
                String[] url = s.split(":");
                redisSentinelConfiguration.sentinel(url[0], Integer.parseInt(url[1]));
            });

            if(password != null && !"".equals(password)){
                redisSentinelConfiguration.setPassword(password);
            }


            return new LettuceConnectionFactory(redisSentinelConfiguration, lettuceClientConfiguration);
        }


        log.info("RedisConnectionFactory standalone mode. host:{}, port:{}", host, port);
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        if(password != null && !"".equals(password)){
            redisStandaloneConfiguration.setPassword(password);
        }
        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
    }

    /**
     * 커넥션풀 설정 생성 * * @return
     */
    private GenericObjectPoolConfig createPoolConfig() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        // 풀이 관리하는 최대 커넥션 개수
        poolConfig.setMaxTotal(redisProperties.getLettuce().getPool().getMaxActive());
        // 풀이 보관할 수 있는 최소 Idle 커넥션 개수
        poolConfig.setMinIdle(redisProperties.getLettuce().getPool().getMinIdle());
        // 풀이 보관할 수 있는 최대 Idle 커넥션 개수
        poolConfig.setMaxIdle(redisProperties.getLettuce().getPool().getMaxIdle());
        // 풀이 관리하는 커넥션이 모두 사용중인 경우에 커넥션 요청
        poolConfig.setBlockWhenExhausted(redisProperties.getLettuce().getPool().getMaxWait().toMillis() > -1);
        // blockWhenExhausted 값이 true 인 경우, 최대 대기 시간을 설정
        poolConfig.setMaxWaitMillis(redisProperties.getLettuce().getPool().getMaxWait().toMillis());

        return poolConfig;
    }


    /*
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            public Object generate(Object target, Method method, Object... params) {
                if (params.length == 0) {
                    return "redis_" + method.getName();
                }
                if (params.length == 1) {
                    Object param = params[0];
                    if (param != null && !param.getClass().isArray()) {
                        return "redis_" + method.getName() + "_" + param;
                    }
                }
                return "redis_" + method.getName() + " [" + StringUtils.arrayToCommaDelimitedString(params) + "]";
            }
        };
    }
    */


    /**
     * serializer가 선언되지 않으면 모든 Object를 java.util.LinkedHashMap으로 저장하여 deserialize가 Object로 수행중 ClassCastException
     *     java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to com.mercury.discovery.base.variable.model.Variable
     * RedisSerializer를 사용하면 동일 Object 변환중 ClassCastException 이 발생하
     *     java.lang.ClassCastException: com.mercury.discovery.base.variable.model.Variable cannot be cast to com.mercury.discovery.base.variable.model.Variable
     *
     * 아래의 링크를 참조하여 JdkSerializationRedisSerializer 로 처리하니 정상 동작
     * //https://stackoverflow.com/questions/48785735/java-lang-classcastexception-cannot-cast-class-to-same-class/49058603
     */
    @Bean
    public RedisSerializer<Object> redisSerializer() {
        return new JdkSerializationRedisSerializer(getClass().getClassLoader());
        /*
        return  new RedisSerializer<Object>() {
            @Override
            public byte[] serialize(Object object) throws SerializationException {
                if (object == null) {
                    return new byte[0];
                }
                if (!(object instanceof Serializable)) {
                    throw new IllegalArgumentException("RedisSerializer.serialize requires a Serializable payload "
                            + "but received an object of type [" + object.getClass().getName() + "]");
                }
                return SerializationUtils.serialize((Serializable) object);
            }

            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                if (bytes == null || bytes.length == 0) {
                    return null;
                }
                return SerializationUtils.deserialize(bytes);
            }
        };
        */
    }

    @Bean
    StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
                                                       ObjectMapper objectMapper, StringRedisSerializer stringRedisSerializer) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, RedisSerializer<Object> redisSerializer) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(connectionFactory);

        RedisCacheConfiguration configuration = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .prefixCacheNameWith("moca:").entryTtl(Duration.ofDays(2L));

        builder.cacheDefaults(configuration);

        //redisCacheManager.setTransactionAware(true);
        return builder.build();
    }

    /*
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory, RedisSerializer<Object> redisSerializer) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setValueSerializer(redisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
    */
}
