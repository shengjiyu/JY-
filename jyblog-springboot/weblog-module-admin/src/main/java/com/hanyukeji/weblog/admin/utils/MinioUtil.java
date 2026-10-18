package com.hanyukeji.weblog.admin.utils;

import com.google.common.collect.ImmutableSet;
import com.hanyukeji.weblog.admin.config.MinioProperties;
import com.hanyukeji.weblog.common.enums.ResponseCodeEnum;
import com.hanyukeji.weblog.common.exception.BizException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
public class MinioUtil {

    /**
     * 允许上传的图片后缀（小写）
     */
    private static final Set<String> ALLOWED_SUFFIXES = ImmutableSet.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".ico", ".bmp");

    @Autowired
    private MinioProperties minioProperties;

    @Autowired
    private MinioClient minioClient;

    /**
     * 上传文件
     *
     * @param file
     * @return 文件访问链接
     * @throws Exception
     */
    public String uploadFile(MultipartFile file) throws Exception {
        // 判断文件是否为空
        if (file == null || file.isEmpty()) {
            log.warn("==> 上传文件异常：文件大小为空 ...");
            throw new BizException(ResponseCodeEnum.FILE_UPLOAD_FAILED);
        }

        // 校验文件后缀（原始文件名可能没有后缀，需判空）
        String originalFileName = file.getOriginalFilename();
        int dotIndex = StringUtils.isBlank(originalFileName) ? -1 : originalFileName.lastIndexOf('.');
        String suffix = dotIndex < 0 ? "" : originalFileName.substring(dotIndex).toLowerCase(Locale.ROOT);
        if (!ALLOWED_SUFFIXES.contains(suffix)) {
            log.warn("==> 上传文件异常：不支持的文件类型, fileName: {}", originalFileName);
            throw new BizException(ResponseCodeEnum.FILE_TYPE_NOT_SUPPORTED);
        }

        // 文件的 Content-Type，缺失时兜底
        String contentType = StringUtils.defaultIfBlank(file.getContentType(), "application/octet-stream");

        // 生成存储对象的名称（将 UUID 字符串中的 - 替换成空字符串），拼接上文件后缀
        String objectName = UUID.randomUUID().toString().replace("-", "") + suffix;

        log.info("==> 开始上传文件至 Minio, ObjectName: {}", objectName);

        // 上传文件至 Minio（try-with-resources 确保流被关闭）
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(contentType)
                    .build());
        }

        // 返回文件的访问链接（去掉 endpoint 末尾多余的 /）
        String endpoint = StringUtils.removeEnd(minioProperties.getEndpoint(), "/");
        String url = String.format("%s/%s/%s", endpoint, minioProperties.getBucketName(), objectName);
        log.info("==> 上传文件至 Minio 成功，访问路径: {}", url);
        return url;
    }
}
