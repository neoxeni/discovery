package com.mercury.discovery.base;

/**
 * base package에서 처리되는 Websocket Message의 경로를 설정한다.
 */
public enum BaseTopic {
    USER("/v2/users/{userKey}"),
    ACTIVE("/v2/active/{userKey}"),
    USERS("/v2/monitor/{clientId}/users"),
    ;
    private final String topic;

    BaseTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return this.topic;
    }

    public String getBindTopic(String... args) {
        String targetPath = this.topic;
        for (String s : args) {
            targetPath = targetPath.replaceFirst("\\{(\\w.*?)}", s);
        }

        return targetPath;
    }
}
