package com.mercury.discovery.config.websocket.message.impl;

import com.mercury.discovery.config.websocket.message.MessagePublisher;
import com.mercury.discovery.config.websocket.message.MessageSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisMessageSubscriber implements MessageSubscriber {
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    public RedisMessageSubscriber(SimpMessageSendingOperations simpMessageSendingOperations, MessagePublisher messagePublisher) {
        this.simpMessageSendingOperations = simpMessageSendingOperations;
        messagePublisher.addSubscriber("/v2/*", this);
    }

    @Override
    public void onMessage(String message, String destination) {
        simpMessageSendingOperations.convertAndSend("/topic" + destination, message);
    }


    /*
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(MessageMapping.class);
        for (Map.Entry<String, Object> elem : beans.entrySet()) {
            Object obj = elem.getValue();
            Class<?> objClz = obj.getClass();
            if (org.springframework.aop.support.AopUtils.isAopProxy(obj)) {
                //As you are using AOP check for AOP proxying. If you are proxying with Spring CGLIB (not via Spring AOP)
                //Use org.springframework.cglib.proxy.Proxy#isProxyClass to detect proxy If you are proxying using JDK
                //Proxy use java.lang.reflect.Proxy#isProxyClass
                objClz = org.springframework.aop.support.AopUtils.getTargetClass(obj);
            }

            String classMessageMappingValue = "";
            MessageMapping classMessageMapping = objClz.getAnnotation(MessageMapping.class);
            if (classMessageMapping != null) {
                classMessageMappingValue = classMessageMapping.value()[0];
            }

            String methodMessageMappingValue;
            for (Method m : objClz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(MessageMapping.class)) {
                    MessageMapping messageMapping = m.getAnnotation(MessageMapping.class);
                    methodMessageMappingValue = classMessageMappingValue + messageMapping.value()[0];

                    this.messagePublisher.addSubscriber(methodMessageMappingValue, this);
                    log.info("MessageMapping {} registered.", methodMessageMappingValue);
                }
            }
        }
    }

    @Override
    public void onMessage(String message, String topic) {
        try {
            MessageWrapper messageWrapper = objectMapper.readValue(message, MessageWrapper.class);
            simpMessageSendingOperations.convertAndSend("/topic" + messageWrapper.getDestination(), messageWrapper.getMessage());
        } catch (IOException e) {
            log.error("onMessage {}, {}", topic, message, e);
        }
    }
    * */
}
