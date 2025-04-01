package gr.atc.urbreath.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinIOConfig {

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.username}")
    private String minioUsername;

    @Value("${minio.password}")
    private String minioPassword;

    @Bean
    public MinioClient minioClient() {
        try {
            return MinioClient.builder()
                    .endpoint(minioUrl)
                    .credentials(minioUsername, minioPassword)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create MinIO client", e);
        }
    }
}
