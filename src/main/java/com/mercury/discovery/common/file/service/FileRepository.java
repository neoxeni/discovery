package com.mercury.discovery.common.file.service;


import com.mercury.discovery.common.file.model.AttachDivCd;
import com.mercury.discovery.common.file.model.AttachFile;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FileRepository {
    List<AttachFile> findFiles(AttachDivCd attachDivCd, String dataNo);

    AttachFile findOne(Integer fileNo);


    int insert(List<AttachFile> attachFileList);

    int delete(List<Integer> deleteFileList);

    List<String> findNotDeletableFileFullPathList(List<String> fileFullPathList);
}
