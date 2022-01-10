package com.mercury.discovery.config.websocket.message;

public interface MessageSubscriber {
    void onMessage(String message, String topic);
}
