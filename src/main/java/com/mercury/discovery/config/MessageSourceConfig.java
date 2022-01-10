package com.mercury.discovery.config;


import com.mercury.discovery.util.MessagesUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;
import java.util.Properties;

/**
 * [Spring MessageSource 와 I18n.js 를 사용한 다국어 처리방법](https://m.blog.naver.com/PostView.nhn?blogId=loveful&logNo=221283232776&proxyReferer=https:%2F%2Fwww.google.com%2F)
 */

@Configuration
public class MessageSourceConfig {
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ExposedResourceMessageBundleSource();
        messageSource.setBasename("classpath:/messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        // 프로퍼티 파일의 변경을 감지할 시간 간격을 지정한다.
        messageSource.setCacheSeconds(60);
        // 없는 메세지일 경우 예외를 발생시키는 대신 코드를 기본 메세지로 한다.
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor() {
        return new MessageSourceAccessor((messageSource()));
    }

    @Bean
    public MessagesUtils messagesUtils() {
        MessagesUtils messagesUtils = new MessagesUtils();
        messagesUtils.setMessageSourceAccessor(messageSourceAccessor());
        return messagesUtils;
    }


    public static class ExposedResourceMessageBundleSource extends ReloadableResourceBundleMessageSource {
        /**
         * Gets all messages for presented Locale.
         *
         * @param locale user request's locale
         * @return all messages
         */
        public Properties getMessages(Locale locale) {
            return getMergedProperties(locale).getProperties();
        }
    }
}

