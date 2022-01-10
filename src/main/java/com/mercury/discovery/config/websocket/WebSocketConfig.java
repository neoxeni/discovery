package com.mercury.discovery.config.websocket;


import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.config.websocket.handler.StompHandler;
import com.mercury.discovery.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

/**
 * <dependency>
 *      <groupId>org.springframework.boot</groupId>
 *      <artifactId>spring-boot-starter-websocket</artifactId>
 * </dependency>
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MessageChannel clientOutboundChannel;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //서버에서 클라이언트로 부터의 메시지를 받을 API의 prefix. client는 /pub/chat/roomname 처럼 메시지를 전송한다.
        config.setApplicationDestinationPrefixes("/pub");

        //메모리 기반 메세지 브로커가 해당 API를 구독하고 있는 클라이언트에게 메시지 전달
        //topic : 암시적으로 1:N 전파를 의미한다.,/queue : 암시적으로 1:1 전파를 의미한다.
        config.enableSimpleBroker("/topic");


        /*
        //registry.enableStompBrokerRelay:SimpleBroker의 기능과 외부 message broker(RabbitMQ, ActiveMQ 등)에 메시지를 전달하는 기능.
        config.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost("192.168.1.100")
                .setRelayPort(61613)
                .setClientLogin("guest")
                .setClientPasscode("guest");
        */
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //client 가 접속할 엔드포인트를 설정하여 열어준다. => var socket = new SockJS('/ws-stomp');
        //sock.js를 통하여 낮은 버전의 브라우저에서도 websocket이 동작


        //springboot 2.4.0 부터 .setAllowedOrigins("*") 안됨..
        //When allowCredentials is true, allowedOrigins cannot contain the special value "*"since that
        //cannot be set on the "Access-Control-Allow-Origin" response header. To allow credentials
        //to a set of origins, list them explicitly or consider using "allowedOriginPatterns" instead.

        //registry.addEndpoint("/ws-stomp").setAllowedOrigins("*").withSockJS();

        registry.addEndpoint("/ws-stomp").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new MessageAppUserMethodArgumentResolver());
    }

    @Bean
    public StompHandler stompHandler() {
        return new StompHandler(clientOutboundChannel, jwtTokenProvider);
    }

    /**
     * redis에 발행(publish)된 메시지 처리를 위한 리스너 설정
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    public class MessageAppUserMethodArgumentResolver implements HandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(MethodParameter methodParameter) {
            return methodParameter.getParameterType().equals(AppUser.class);
        }

        @Override
        public Object resolveArgument(MethodParameter methodParameter, Message<?> message) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            String authorization = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

            if (StringUtils.hasLength(authorization) && authorization.startsWith("bearer ")) {
                return jwtTokenProvider.getUserFromJwt(authorization);
            }

            return jwtTokenProvider.triggerException(authorization);
        }
    }
}
