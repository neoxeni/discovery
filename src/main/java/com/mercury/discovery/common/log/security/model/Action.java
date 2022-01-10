package com.mercury.discovery.common.log.security.model;

import com.mercury.discovery.utils.ContextUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of={"method","path"})
@XmlRootElement(name = "action")
@XmlAccessorType(XmlAccessType.FIELD)
public class Action {
    @XmlAttribute(name = "method")
    private String method;

    @XmlJavaTypeAdapter(EnvironmentResolveAdapter.class)
    @XmlAttribute(name = "path")
    private String path;

    @XmlElement(name = "meta")
    private Meta meta;


    public static class EnvironmentResolveAdapter extends XmlAdapter<String, String> {
        @Override
        public String unmarshal(String s) throws Exception {
            return ContextUtils.getEnvironment().resolveRequiredPlaceholders(s);
        }

        @Override
        public String marshal(String s) throws Exception {
            return s;
        }
    }
}
