package com.hanyukeji.weblog.admin.service.impl;

import com.hanyukeji.weblog.admin.model.vo.file.UploadFileRspVO;
import com.hanyukeji.weblog.admin.service.AdminFileService;
import com.hanyukeji.weblog.admin.utils.MinioUtil;
import com.hanyukeji.weblog.common.enums.ResponseCodeEnum;
import com.hanyukeji.weblog.common.exception.BizException;
import com.hanyukeji.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.SSLException;

@Service
@Slf4j
public class AdminFileServiceImpl implements AdminFileService {
    @Autowired
    private MinioUtil minioUtil;

    @Override
    public Response uploadFile(MultipartFile file) {
        try {
            // 上传文件
            String url = minioUtil.uploadFile(file);

            // 构建成功返参，将图片的访问链接返回
            return Response.success(UploadFileRspVO.builder().url(url).build());
        } catch (BizException e) {
            // 参数类业务异常（文件为空、类型不支持等）直接抛出
            throw e;
        } catch (SSLException e) {
            // 典型场景：Minio 访问域名的 HTTPS 证书过期 / 不受信任
            log.error("==> 上传文件至 Minio 错误（SSL 证书校验失败，请检查 minio.endpoint 对应域名的证书是否过期）: ", e);
            throw new BizException(ResponseCodeEnum.FILE_STORAGE_SSL_ERROR);
        } catch (Exception e) {
            log.error("==> 上传文件至 Minio 错误: ", e);
            // 手动抛出业务异常，提示 “文件上传失败”
            throw new BizException(ResponseCodeEnum.FILE_UPLOAD_FAILED);
        }
    }
}
