package codeking.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云 OSS（SDK V2）连接信息。AccessKey 请用环境变量，勿写入配置文件。
 */
@Data
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOssProperties {

    private String endpoint;
    private String region;
    private String bucket;

    /**
     * 可选。手动指定返回给前端的访问前缀（如 CDN / 自定义域）。
     * 不配置时由服务端根据 bucket + endpoint 拼虚拟主机风格 URL。
     */
    private String urlPrefix;
}
