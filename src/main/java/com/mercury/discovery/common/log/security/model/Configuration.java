package com.mercury.discovery.common.log.security.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Optional;

@Getter
@Setter
@ToString
@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class Configuration {
    @XmlElement(name = "meta-mapping")
    private MetaMapping metaMapping;
    @XmlElement(name = "exclude")
    private Exclude exclude;
    @XmlAttribute(name = "active")
    private boolean active;
    @XmlJavaTypeAdapter(ActiveProfilesResolveAdapter.class)
    @XmlAttribute(name = "activeProfiles")
    private String[] activeProfiles;

    @XmlElement(name = "request-param")
    private RequestParam requestParam;

    public boolean isExclude(AntPathMatcher antPathMatcher, Action action) {

        return exclude.getActions().stream()
                .filter(e -> antPathMatcher.match(e.getPath(), action.getPath())
                        && (e.getMethod().equalsIgnoreCase(action.getMethod()) || e.getMethod().equals("*")))
                .findAny().isPresent();
    }

    public Meta getMeta(Action action) {
        Optional<Action> optional = metaMapping.getActions().stream()
                .filter(e -> e.getPath().equalsIgnoreCase(action.getPath())
                        && (e.getMethod().equalsIgnoreCase(action.getMethod()) || e.getMethod().equals("*")))
                .findFirst();

        if (!optional.isPresent()) {
            return null;
        }
        return optional.get().getMeta();
    }

    public static class ActiveProfilesResolveAdapter extends XmlAdapter<String, String[]> {
        @Override
        public String[] unmarshal(String s) throws Exception {
            return StringUtils.tokenizeToStringArray(s, ",");
        }

        @Override
        public String marshal(String[] strings) throws Exception {
            return StringUtils.arrayToCommaDelimitedString(strings);
        }
    }
}