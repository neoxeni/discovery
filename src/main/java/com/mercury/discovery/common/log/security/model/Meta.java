package com.mercury.discovery.common.log.security.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@ToString
@XmlRootElement(name = "meta")
@XmlAccessorType(XmlAccessType.FIELD)
public class Meta {
    @XmlAttribute(name = "menu")
    private String menu;
    @XmlAttribute(name = "submenu")
    private String submenu;
    @XmlAttribute(name = "div")
    private String div;
    @XmlAttribute(name = "etc1")
    private String etc1;
    @XmlAttribute(name = "etc2")
    private String etc2;
    @XmlAttribute(name = "etc3")
    private String etc3;
    @XmlAttribute(name = "etc4")
    private String etc4;
    @XmlAttribute(name = "etc5")
    private String etc5;
}
