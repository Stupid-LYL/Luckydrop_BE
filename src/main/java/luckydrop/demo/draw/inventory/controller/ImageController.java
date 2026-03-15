package luckydrop.demo.draw.inventory.controller;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.draw.inventory.dto.ImageUploadResponse;
import luckydrop.demo.draw.inventory.service.ImageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadImages(
            @RequestPart("images") List<MultipartFile> images
            ) {
        List<String> imageUrls = imageService.uploadImages(images);
        return ResponseEntity.ok(new ImageUploadResponse(imageUrls));
    }

}
