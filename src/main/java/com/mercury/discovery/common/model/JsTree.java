package com.mercury.discovery.common.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class JsTree implements Serializable {
    private static final long serialVersionUID = 6489222024366253477L;

    private String id;
    private String parent;
    private String text;

    private String icon; // mdi mdi-folder  (mdi 아이콘 이름)
    private String type; // folder          (jsTree types 이름)

    private String dataType;
    private Object data;

    private String divCd;
    private Integer cmpnyNo;
}
