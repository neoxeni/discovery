package com.mercury.discovery.common.log.security.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.*;
import java.util.List;

@Getter
@Setter
@ToString
@XmlRootElement(name = "exclude")
@XmlAccessorType(XmlAccessType.FIELD)
public class Exclude {
    @XmlElementWrapper(name="actions")
    @XmlElement(name="action")
    private List<Action> actions;
}
