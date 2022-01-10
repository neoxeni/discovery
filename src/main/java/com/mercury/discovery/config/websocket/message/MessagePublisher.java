package com.mercury.discovery.config.websocket.message;

public interface MessagePublisher {

    void convertAndSend(String destination, String... params);

    void convertAndSend(String destination, Object message);

    void addSubscriber(String topic, MessageSubscriber subscriber);

    default String bind(String path, String... args){
        String targetPath = path;
        for (String s : args) {
            targetPath = targetPath.replaceFirst("\\{(\\w.*?)}", s);
        }

        return targetPath;
    }
}