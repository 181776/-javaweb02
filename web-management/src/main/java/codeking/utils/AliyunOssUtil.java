package codeking.utils;

import codeking.config.AliyunOssProperties;
import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.OSSClientBuilder;
import com.aliyun.sdk.service.oss2.credentials.CredentialsProvider;
import com.aliyun.sdk.service.oss2.credentials.EnvironmentVariableCredentialsProvider;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest;
import com.aliyun.sdk.service.oss2.models.PutObjectResult;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 基于 OSS Java SDK V2 的上传工具，凭证从环境变量读取（与官方示例一致）。
 *
 * <p>V2 的 {@link OSSClient} 在构造/关闭链路会声明抛出 {@link Exception}，因此对外方法一并声明，
 * 避免 IDE/编译器报「try-with-resources 未处理的可关闭资源异常」。
 */
@Component
@RequiredArgsConstructor
public class AliyunOssUtil {

    private final AliyunOssProperties ossProperties;

    /**
     * 上传到配置中的默认 Bucket。
     */
    public PutObjectResult uploadBytes(String objectKey, byte[] content) throws Exception {
        return uploadBytes(ossProperties.getBucket(), objectKey, content);
    }

    /**
     * 指定 Bucket 上传。
     */
    public PutObjectResult uploadBytes(String bucket, String objectKey, byte[] content) throws Exception {
        CredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider();
        OSSClientBuilder clientBuilder = OSSClient.newBuilder()
                .credentialsProvider(credentialsProvider)
                .region(ossProperties.getRegion())
                .endpoint(ossProperties.getEndpoint());

        try (OSSClient client = clientBuilder.build()) {
            return client.putObject(PutObjectRequest.newBuilder()
                    .bucket(bucket)
                    .key(objectKey)
                    .body(BinaryData.fromBytes(content))
                    .build());
        }
    }

    public PutObjectResult uploadMultipart(String objectKey, MultipartFile file) throws Exception {
        return uploadBytes(objectKey, file.getBytes());
    }
}
