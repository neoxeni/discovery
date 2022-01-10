package com.mercury.discovery.config.web;

import com.mercury.discovery.base.users.service.handler.UserArgumentResolver;
import com.mercury.discovery.common.excel.model.mapper.ExcelModelArgumentResolver;
import com.mercury.discovery.common.model.date.DateRangeMethodArgumentResolver;
import com.mercury.discovery.config.web.gzip.GZIPFilter;
import com.mercury.discovery.config.web.gzip.GZIPProperties;
import com.mercury.discovery.config.web.resolver.UploadFileResourceResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.FormContentFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Configuration
@EnableWebMvc
@EnableConfigurationProperties(GZIPProperties.class)
public class ServletContextConfig implements WebMvcConfigurer {
    @Autowired
    private WebMvcProperties webMvcProperties;

    @Value("${apps.upload.publicUrl}")
    private String publicUrl;

    @Value("${apps.upload.root}")
    private String uploadRoot;

    @Value("${apps.upload.allowedAccessExtensions:}")
    private String allowedAccessExtensions;

    @Value("${apps.request-mapping}")
    private String requestMapping;

    @Qualifier("GZIPProperties")
    @Autowired
    private GZIPProperties gzipProperties;

    @Bean
    public BeanNameViewResolver beanNameViewResolver() {
        return new BeanNameViewResolver();
    }

    /**
     * 변경된 언어 정보를 기억할 로케일 리졸버를 생성한다.
     * 여기서는 쿠키에 저장하는 방식을 사용한다.
     */
    @Bean
    public LocaleResolver localeResolver() {
        //default AcceptHeaderLocaleResolver
        //CookieLocaleResolver
        return new CookieLocaleResolver();
    }

    @Bean // 지역설정을 변경하는 인터셉터. 요청시 파라미터에 lang 정보를 지정하면 언어가 변경됨.
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new UserArgumentResolver());
        resolvers.add(new DateRangeMethodArgumentResolver());
        resolvers.add(new ExcelModelArgumentResolver());

        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }

    //https://moonscode.tistory.com/125
    //https://dzone.com/articles/2-step-resource-versioning-with-spring-mvc
    //-Dspring.profiles.active=prod
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Duration cachePeriodDuration = Duration.ofSeconds(2147483647);
        Integer cachePeriod = cachePeriodDuration != null ? (int) cachePeriodDuration.getSeconds() : 0;//0이면 캐쉬 안함
        boolean resourceChain = false;

        if (!registry.hasMappingForPattern("/webjars/**")) {
            registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/")
                    .setCachePeriod(cachePeriod).resourceChain(resourceChain);
        }

        String staticPathPattern = webMvcProperties.getStaticPathPattern();

        //static 폴더 cache관련 설정 제거
        VersionResourceResolver versionResourceResolver = new VersionResourceResolver().addContentVersionStrategy("/**");

        registry.addResourceHandler(staticPathPattern)
                .addResourceLocations("/static")
                .setCachePeriod(cachePeriod)
                .resourceChain(resourceChain)
                .addResolver(versionResourceResolver);

        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/static/favicon.ico")
                .setCachePeriod(cachePeriod).resourceChain(resourceChain);

        Set<String> allowedExtensionSet = new HashSet<>(Arrays.asList(allowedAccessExtensions.split(",")));
        registry.addResourceHandler(publicUrl + "/**")
                .addResourceLocations(uploadRoot + publicUrl + "/")
                .setCachePeriod(cachePeriod)
                .resourceChain(resourceChain)
                .addResolver(new UploadFileResourceResolver(allowedExtensionSet));
    }


    /**
     * https://stackoverflow.com/questions/19600532/modelattribute-for-restful-put-method-not-populated-value-json
     * form 데이터의 경우 put, patch 등은 ModelAttribute가 적용되지 않아 FormContentFilter 또는 HiddenHttpMethodFilter를 등록
     */
    @Bean
    public FilterRegistrationBean<FormContentFilter> formContentFilterBean() {
        //parses form data for HTTP PUT, PATCH, and DELETE requests
        FilterRegistrationBean<FormContentFilter> filterRegistration = new FilterRegistrationBean<>();
        filterRegistration.setFilter(new FormContentFilter());
        filterRegistration.addUrlPatterns("/*");

        return filterRegistration;
    }

    @Bean
    public FilterRegistrationBean<GZIPFilter> gzipFilter() {
        FilterRegistrationBean<GZIPFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new GZIPFilter());

        Set<String> urlPatterns = null;
        if (gzipProperties != null) {
            urlPatterns = gzipProperties.getFilterPatterns();
        }
        if (urlPatterns == null) {
            urlPatterns = new HashSet<>();
        }

        urlPatterns.add(requestMapping + "/base/codes/scripts/*");

        registrationBean.addUrlPatterns(urlPatterns.toArray(new String[0]));

        return registrationBean;
    }
}
