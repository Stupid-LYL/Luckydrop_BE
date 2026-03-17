package luckydrop.demo.draw.inventory.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<String> uploadImages(List<MultipartFile> images) {

        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("업로드할 이미지가 없습니다.");
        }

        List<String> imageUrls = new ArrayList<>();

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            for (MultipartFile image : images) {

                System.out.println("file = " + image.getOriginalFilename());
                System.out.println("type = " + image.getContentType());

                if (image.isEmpty()) {
                    continue;
                }

                validateImage(image);

                String originalFilename = image.getOriginalFilename();
                if (originalFilename == null || originalFilename.isBlank()) {
                    throw new IllegalArgumentException("원본 파일명이 없습니다.");
                }
                originalFilename = StringUtils.cleanPath(image.getOriginalFilename());

                String extension = extractExtension(originalFilename);
                String storedFilename = UUID.randomUUID() + "." + extension;

                Path targetPath = uploadPath.resolve(storedFilename);
                Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                String imageUrl = "/uploads/" + storedFilename;
                imageUrls.add(imageUrl);
            }

            return imageUrls;

        } catch(IOException e) {
            throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
        }
    }


    private void validateImage(MultipartFile image) {
        String contentType = image.getContentType();
        if(contentType == null) {
            throw new IllegalArgumentException("파일 타입을 확인할 수 없습니다.");
        }

        List<String> allowedTypes = List.of("image/jpeg", "image/png", "image/gif", "image/webp");
        if (!allowedTypes.contains(contentType)) {
            throw new IllegalArgumentException("jpg, png, gif, webp 이미지만 업로드 가능합니다.");
        }

        long maxSize = 5 * 1024 * 1024; // 5MB
        if (image.getSize() > maxSize) {
            throw new IllegalArgumentException("이미지 크기는 5MB 이하여야 합니다.");
        }
    }

    private String extractExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        System.out.println("filename = " + filename);
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            throw new IllegalArgumentException("파일 확장자가 없습니다.");
        }
        return filename.substring(lastDotIndex + 1);
    }

}
