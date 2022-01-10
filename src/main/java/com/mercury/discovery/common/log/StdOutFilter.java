package com.mercury.discovery.common.log;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

import java.util.Map;

/**
 * 특정 구간 에서 로그 print (STDOUT) 를 스킵 하기 위한 필터.
 * LocalThread 영역 에서만 MDC가 유지 되므로 다른 쓰레드로 수행되는 부분에서는 유지하지 못함.
 *
 * MDC.put(StdOutFilter.SKIP_STD_OUT, "true"); << 로그 스킵 START
 * ...
 * ... Business Logic..
 * ...
 * ...
 * MDC.clear(); << 로그 스킵 END
 */

public class StdOutFilter extends AbstractMatcherFilter {

    public static final String SKIP_STD_OUT = "skip.log.console";


    @Override
    public FilterReply decide(Object event) {

        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        LoggingEvent loggingEvent = (LoggingEvent) event;

        Map mdcMap = loggingEvent.getMDCPropertyMap();

        if (mdcMap == null || mdcMap.isEmpty()) {
            return FilterReply.NEUTRAL;
        }

        if ("true".equals(mdcMap.get(SKIP_STD_OUT))) {
            return FilterReply.DENY;
        }

        return FilterReply.NEUTRAL;

    }
}
