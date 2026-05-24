package codeking;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.OSSClientBuilder;
import com.aliyun.sdk.service.oss2.credentials.CredentialsProvider;
import com.aliyun.sdk.service.oss2.credentials.EnvironmentVariableCredentialsProvider;
import com.aliyun.sdk.service.oss2.models.BucketSummary;
import com.aliyun.sdk.service.oss2.models.ListBucketsRequest;
import com.aliyun.sdk.service.oss2.models.ListBucketsResult;
import com.aliyun.sdk.service.oss2.paginator.ListBucketsIterable;

/**
 * 本地试跑用：验证环境变量里的 AK 与 region 是否能访问 OSS。
 * 在 IDE 中右键本类 → Run；不要当作线上接口暴露。
 */
public class OssListBucketsExample {

    public static void main(String[] args) {
        // 改成你的 Bucket 所在地域，例如 cn-beijing、cn-hangzhou
        String region = "cn-beijing";

        CredentialsProvider provider = new EnvironmentVariableCredentialsProvider();
        OSSClientBuilder clientBuilder = OSSClient.newBuilder()
                .credentialsProvider(provider)
                .region(region);

        try (OSSClient client = clientBuilder.build()) {
            ListBucketsIterable paginator = client.listBucketsPaginator(
                    ListBucketsRequest.newBuilder()
                            .build());

            for (ListBucketsResult result : paginator) {
                for (BucketSummary info : result.buckets()) {
                    System.out.printf("bucket: name:%s, region:%s, storageClass:%s%n",
                            info.name(), info.region(), info.storageClass());
                }
            }
        } catch (Exception e) {
            System.out.printf("error:%n%s%n", e);
        }
    }
}
