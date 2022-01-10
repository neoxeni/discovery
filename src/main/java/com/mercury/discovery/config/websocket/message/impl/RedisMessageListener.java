package com.mercury.discovery.config.websocket.message.impl;

import com.mercury.discovery.config.websocket.message.MessageSubscriber;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;


public class RedisMessageListener implements MessageListener {
    private final MessageSubscriber subscriber;

    public RedisMessageListener(MessageSubscriber subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        subscriber.onMessage(message.toString(), new String(message.getChannel()));
    }
}
