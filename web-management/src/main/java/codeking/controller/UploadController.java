package codeking.controller;

import codeking.config.AliyunOssProperties;
import codeking.pojo.Result;
import codeking.utils.AliyunOssUtil;
import com.aliyun.sdk.service.oss2.models.PutObjectResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UploadController {

    private final AliyunOssUtil aliyunOssUtil;
    private final AliyunOssProperties ossProperties;
    /**
     * 上传文件 - 参数名file（对象写入阿里云 OSS）
     */
    @PostMapping("/upload")
    public Result upload(String username, Integer age, MultipartFile file) throws Exception {
        log.info("上传文件：{}, {}, {}", username, age, file);
        if (file.isEmpty()) {
            return Result.success();
        }
        String originalFilename = file.getOriginalFilename();
        String extName = "";
        if (originalFilename != null && originalFilename.lastIndexOf('.') >= 0) {
            extName = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String objectKey = UUID.randomUUID().toString().replace("-", "") + extName;

        PutObjectResult putResult = aliyunOssUtil.uploadMultipart(objectKey, file);

        log.info("OSS上传成功 objectKey={}, eTag={}, requestId={}",
                objectKey, putResult.eTag(), putResult.requestId());

        String url = buildAccessibleUrl(objectKey);
        return Result.success(Map.of(
                "objectKey", objectKey,
                "url", url,
                "eTag", putResult.eTag() != null ? putResult.eTag() : ""));
    }

    /**
     * 供前端直接展示的访问地址（公有读 Bucket 或 CDN）。
     */
    private String buildAccessibleUrl(String objectKey) {
        String prefix = ossProperties.getUrlPrefix();
        if (prefix != null && !prefix.isBlank()) {
            String p = prefix.trim().replaceAll("/+$", "");
            return p + "/" + objectKey;
        }
        String endpoint = ossProperties.getEndpoint();
        String bucket = ossProperties.getBucket();
        if (endpoint == null || bucket == null || objectKey == null) {
            return "";
        }
        String host = endpoint.trim();
        if (host.startsWith("https://")) {
            host = host.substring("https://".length());
        } else if (host.startsWith("http://")) {
            host = host.substring("http://".length());
        }
        host = host.replaceAll("/+$", "");
        return "https://" + bucket + "." + host + "/" + objectKey;
    }
}
