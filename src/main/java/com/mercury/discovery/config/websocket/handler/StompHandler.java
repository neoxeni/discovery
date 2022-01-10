package com.mercury.discovery.config.websocket.handler;

import com.mercury.discovery.base.users.model.TokenUser;
import com.mercury.discovery.utils.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    private final MessageChannel clientOutboundChannel;
    private final JwtTokenProvider jwtTokenProvider;

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand currentCommand = accessor.getCommand();
        TokenUser tokenUser = null;
        if (currentCommand != StompCommand.DISCONNECT && currentCommand != StompCommand.UNSUBSCRIBE) {
            //DISCONNECT, UNSUBSCRIBE가 아닌 이상은 TOKEN을 검증한다.
            String jwt = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
            if (jwt == null) {
                sendErrorMessageToClient("authorization header required. must follow the scheme 'authorization bearer jwt'", accessor.getSessionId());
                return null;
            }

            try {
                tokenUser = jwtTokenProvider.getUserFromJwt(jwt);
            } catch (JwtException e) {
                String errMessage = "request token이 유효하지 않습니다.";
                if (e instanceof ExpiredJwtException) {
                    errMessage = "request token이 만료되었습니다.";
                }
                sendErrorMessageToClient(errMessage, accessor.getSessionId());
                return null;
            }

        }

        printPreSendLog(message, accessor, tokenUser);
        return message;
    }

    private void sendErrorMessageToClient(String message, String sessionId) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
        headerAccessor.setMessage(message);
        headerAccessor.setSessionId(sessionId);
        this.clientOutboundChannel.send(MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders()));
    }

    private void printPreSendLog(Message<?> message, StompHeaderAccessor accessor, TokenUser tokenUser) {
        if (log.isTraceEnabled()) {
            StompCommand currentCommand = accessor.getCommand();
            StringBuilder sb = new StringBuilder();
            sb.append(currentCommand);
            if (tokenUser != null) {
                sb.append(tokenUser.getId()).append(" ");
            }

            sb.append(accessor.getDestination()).append(" ");
            sb.append(message);
            log.trace(sb.toString());
        }
    }

    /*private void saveApiUserToSessionIfNull(StompHeaderAccessor accessor) {
        Map<String, Object> attributes = accessor.getSessionAttributes();
        if (attributes != null) {
            TokenUser user = (TokenUser) attributes.get(TokenUser.USER_SESSION_KEY);

            if (user == null) {
                String jwt = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
                if (StringUtils.hasLength(jwt)) {
                    user = jwtTokenProvider.getUserFromJwt(jwt);
                    attributes.put(TokenUser.USER_SESSION_KEY, user);
                }
            }
        }
    }*/

    /*
    private final static String SE_ATTR_NAME_SUBSCRIBE_MAP_KEY = "subscribeMap";
    private void saveApiUserSubscription(StompHeaderAccessor accessor) {
        Map<String, Object> attributes = accessor.getSessionAttributes();
        if (attributes != null) {
            //noinspection unchecked
            Map<String, String> subscribeMap = (Map<String, String>) attributes.get(SE_ATTR_NAME_SUBSCRIBE_MAP_KEY);
            if (subscribeMap == null) {
                subscribeMap = new HashMap<>();
                attributes.put(SE_ATTR_NAME_SUBSCRIBE_MAP_KEY, subscribeMap);
            }
            String subId = accessor.getFirstNativeHeader("id");
            String subDestination = accessor.getFirstNativeHeader("destination");
            subscribeMap.put(subDestination, subId);
        }
    }

    private ApiUser getValidTokenUser(StompHeaderAccessor accessor) {
        String jwt = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
        if (jwt != null) {
            Map<String, Object> attributes = accessor.getSessionAttributes();
            if (attributes != null) {
                ApiUser user = (ApiUser) attributes.get(ApiUser.USER_SESSION_KEY);
                long currentTimeMillis = System.currentTimeMillis();

                if (jwt.equals(user.getJwt())) {//token값이 변경되지 않았다면
                    if (currentTimeMillis < user.getExpiredAt()) {
                        return user;
                    }
                } else {//token이 접속시 들고 있는 값과 다르다면 (갱신등에 의해 변경)
                    ApiUser refreshApiUser = jwtTokenProvider.getUserFromJwt(jwt);
                    if (user.equals(refreshApiUser)) {
                        if (currentTimeMillis < refreshApiUser.getExpiredAt()) {

                            attributes.put(ApiUser.USER_SESSION_KEY, refreshApiUser);
                            return refreshApiUser;
                        }
                    }
                }
            }
        }

        jwtTokenProvider.triggerException(authorization);
    }
    */
}
