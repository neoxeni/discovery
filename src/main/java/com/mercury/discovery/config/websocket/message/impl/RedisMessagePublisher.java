package com.mercury.discovery.config.websocket.message.impl;

import com.mercury.discovery.common.error.exception.BadParameterException;
import com.mercury.discovery.config.websocket.message.MessagePublisher;
import com.mercury.discovery.config.websocket.message.MessageSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;

import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessagePublisher implements MessagePublisher {
    private final RedisTemplate<?, ?> redisTemplate;
    private final RedisMessageListenerContainer redisMessageListenerContainer;


    @Override
    public void convertAndSend(String destination, String... params) {
        if (params.length % 2 != 0) {
            throw new BadParameterException("params length is invalid");
        }

        Map<String, String> param = IntStream.range(0, params.length / 2).boxed()
                .collect(Collectors.toMap(i -> params[i * 2], i -> params[i * 2 + 1]));

        redisTemplate.convertAndSend(destination, param);
    }

    @Override
    public void convertAndSend(String destination, Object message) {

        /*Object param = message;
        if (message instanceof JsonElement) {
            try {
                param = objectMapper.readValue(message.toString(), Map.class);
            } catch (JsonProcessingException e) {
                log.error("convertAndSend", e);
            }
        }*/

        redisTemplate.convertAndSend(destination, message);
    }

    @Override
    public void addSubscriber(String topic, MessageSubscriber subscriber) {
        //ChannelTopic, PatternTopic
        redisMessageListenerContainer.addMessageListener(new RedisMessageListener(subscriber), new PatternTopic(topic));
        log.debug("topic {} registered {}", topic, subscriber);
    }
}

