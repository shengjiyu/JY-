package com.hanyukeji.weblog.admin.utils;

import com.hanyukeji.weblog.common.enums.ResponseCodeEnum;
import com.hanyukeji.weblog.common.exception.BizException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 上传文件校验（校验失败时不会访问 Minio，无需真实服务）
 */
class MinioUtilTest {

    private final MinioUtil minioUtil = new MinioUtil();

    @Test
    void emptyFileRejected() {
        MockMultipartFile file = new MockMultipartFile("file", "a.png", "image/png", new byte[0]);
        BizException e = assertThrows(BizException.class, () -> minioUtil.uploadFile(file));
        assertEquals(ResponseCodeEnum.FILE_UPLOAD_FAILED.getErrorCode(), e.getErrorCode());
    }

    @Test
    void fileWithoutSuffixRejected() {
        MockMultipartFile file = new MockMultipartFile("file", "avatar", "image/png", new byte[]{1});
        BizException e = assertThrows(BizException.class, () -> minioUtil.uploadFile(file));
        assertEquals(ResponseCodeEnum.FILE_TYPE_NOT_SUPPORTED.getErrorCode(), e.getErrorCode());
    }

    @Test
    void nonImageRejected() {
        MockMultipartFile file = new MockMultipartFile("file", "evil.html", "text/html", new byte[]{1});
        BizException e = assertThrows(BizException.class, () -> minioUtil.uploadFile(file));
        assertEquals(ResponseCodeEnum.FILE_TYPE_NOT_SUPPORTED.getErrorCode(), e.getErrorCode());
    }
}
