package gr.atc.urbreath.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@AllArgsConstructor
public class MinIOStorageService {

    private final MinioClient minioClient;

    /**
     * Upload a file to MinIO
     *
     * @param file : MultiPart File
     * @param bucketName : Bucket Name
     * @return File Name
     */
    public String uploadFile(MultipartFile file, String nbsTitle, String bucketName) {
        // Validate inputs before processing
        if (file == null || StringUtils.isEmpty(bucketName)) {
            throw new IllegalArgumentException("File or bucket name cannot be null");
        }

        try {
            String fileName = nbsTitle + "_" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("File upload to MinIO failed", e);
        }
    }



}
