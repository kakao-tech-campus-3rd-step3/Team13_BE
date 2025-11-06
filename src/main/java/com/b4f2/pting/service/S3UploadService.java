package com.b4f2.pting.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String saveImage(@Nullable MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return amazonS3.getUrl(bucket, "default.jpeg").toString();
        }

        String originalFilename = image.getOriginalFilename();
        String extension = extractExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString().replaceAll("-", "") + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getSize());
        metadata.setContentType(image.getContentType());

        try {
            amazonS3.putObject(bucket, uniqueFilename, image.getInputStream(), metadata);

            return amazonS3.getUrl(bucket, uniqueFilename).toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String extractExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf(".");
        return (dotIndex != -1) ? filename.substring(dotIndex) : "";
    }
}
