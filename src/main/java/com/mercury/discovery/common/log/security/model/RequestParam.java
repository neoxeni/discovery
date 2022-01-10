package com.mercury.discovery.common.log.security.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.*;
import java.util.List;

@Getter
@Setter
@ToString
@XmlRootElement(name = "request-param")
@XmlAccessorType(XmlAccessType.FIELD)
public class RequestParam {

    @XmlAttribute(name = "save")
    private boolean save;

    @XmlAttribute(name = "include-value")
    private String includeValue;

    @XmlElementWrapper(name="ignore")
    @XmlElement(name="key")
    private List<String> ignore;

}
