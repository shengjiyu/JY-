package com.hanyukeji.weblog.admin.service;

import com.hanyukeji.weblog.common.utils.Response;
import org.springframework.web.multipart.MultipartFile;

public interface AdminFileService {
    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    Response uploadFile(MultipartFile file);

}
