package EzyShop.controller;

import EzyShop.dto.ImageInfo;
import EzyShop.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    // Upload endpoint
    @PostMapping("/upload/images")
    public ResponseEntity<List<ImageInfo>> uploadImages(@RequestParam("files") MultipartFile[] files)
            throws IOException {
        List<ImageInfo> result = new ArrayList<>();

        for (MultipartFile file : files) {
            String filename = imageService.saveImage(file);
            String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/backend/api/v1/images/view/")
                    .path(filename)
                    .toUriString();
            result.add(new ImageInfo(filename, url));

        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/upload/image")
    public ResponseEntity<ImageInfo> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String filename = imageService.saveImage(file);
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/backend/api/v1/images/view/")
                .path(filename)
                .toUriString();
        return ResponseEntity.ok(new ImageInfo(filename, url));
    }

    @PostMapping("/upload/thumbnail")
    public ResponseEntity<ImageInfo> uploadThumbnail(@RequestParam("file") MultipartFile file) throws IOException {
        String filename = imageService.saveImage(file);
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/backend/api/v1/images/view/")
                .path(filename)
                .toUriString();
        return ResponseEntity.ok(new ImageInfo(filename, url));
    }

    // View endpoint
    @GetMapping("/view/{filename}")
    public ResponseEntity<Resource> viewImage(@PathVariable String filename) throws IOException {
        // ambil path home user
        String userHome = System.getProperty("user.home");
        Path imagePath = Paths.get(userHome, "uploads", "img", filename);

        log.info("Viewing image: {}", Files.exists(imagePath));
        if (!Files.exists(imagePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(imagePath.toUri());
        String contentType = Files.probeContentType(imagePath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .body(resource);
    }

}
