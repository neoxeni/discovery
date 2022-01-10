package com.mercury.discovery.common.file.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.mercury.discovery.util.ContextUtils;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.time.LocalDateTime;

@Alias("AttachFile")
@Data
public class AttachFile implements Serializable {
    private static final long serialVersionUID = 4614761186868238353L;
    private Integer fileNo;
    private String fileNm;
    private String filePath;
    private long fileSize;
    private String extNm;
    private String userFileNm;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updDt;

    private AttachDivCd attachDivCd;
    private String dataNo;

    private int noteNo;
    private int cmpnyNo;

    private String meta;
    private String workType;//INSERT, DELETE

    // file view url
    // 추후 cloud 환경 등으로 변경될 소지 있음.
    public String getUrl() {

        FileSystemScheme scheme = FileSystemScheme.fromString(ContextUtils.getEnvironmentProperty("apps.upload.fileSystem"));

        if (scheme == FileSystemScheme.S3) {
            // aws-s3 프로토콜인 경우 외부 url 리턴
            String bucket = ContextUtils.getEnvironmentProperty("aws.bucket");
            String region = ContextUtils.getEnvironmentProperty("aws.region");

            return String.format("https://%s.s3.%s.amazonaws.com%s/%s", bucket, region, filePath, fileNm);
        }

        String host = ContextUtils.getEnvironmentProperty("apps.host");
        String requestMapping = ContextUtils.getEnvironmentProperty("apps.request-mapping");
        return host + requestMapping + "/base/files/view/" + this.fileNo;
    }
}
