package com.mercury.discovery.common.file.model;


import org.springframework.util.StringUtils;

public enum AttachDivCd {
    ISMHAT01("VOC 접수"),
    ISMHAT02("VOC 처리"),
    ISMHAT03("VOC 개선"),
    ISMHAT04("협의회회의등록"),
    ISMHAT05("개선과제등록"),
    ISMHAT06("개선과제계획"),
    ISMHAT07("개선추진등록"),
    ISMHAT08("개선과제완료"),
    ISMHAT09("위원회상정의견"),
    ISMHAT10("위원회과제선정"),
    ISMAAT11("이메일첨부"),
    ISMAAT12("CALL 상담"),
    ISMAAT13("일반게시물"),
    ISMAAT14("임시게시물"),
    ISMAAT15("상담사사진전송"),
    ISMAAT16("고객사진전송"),
    ISMAAT17("쪽지"),
    ISMAAT18("지식게시물"),
    ISMAAT19("시험평가"),
    ISMAAT20("메일"),


    ISMAAT31("CHAT 상담"),
    ISMAAT32("EMAIL 상담"),
    ISMAAT33("HOMEPAGE 상담"),

    ISMAAT34("에디터 이미지"),


    ISMAAT99("ETC"),


    ;

    private String cdNm;

    AttachDivCd(String cdNm) {
        this.cdNm = cdNm;
    }

    public String getCdNm() {
        return cdNm;
    }

    public String getLabel() {
        return cdNm;
    }

    public static AttachDivCd fromString(String text) {

        if(StringUtils.isEmpty(text)) {
            return AttachDivCd.ISMAAT99;
        }

        for (AttachDivCd attachDivCd : AttachDivCd.values()) {
            if (attachDivCd.toString().equalsIgnoreCase(text)) {
                return attachDivCd;
            }
        }
        return AttachDivCd.ISMAAT99;
    }
}
