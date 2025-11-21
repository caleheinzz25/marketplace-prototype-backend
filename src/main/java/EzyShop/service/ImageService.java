package EzyShop.service;

import EzyShop.exception.BusinessException;
import EzyShop.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Slf4j
@Service
public class ImageService {

    @Value("${image.upload.dir}")
    private String uploadDir;

    public String saveImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("Uploaded file must not be empty", HttpStatus.BAD_REQUEST);
        }

        try {
            String originalFilename = Paths.get(file.getOriginalFilename()).getFileName().toString();
            String filename = System.currentTimeMillis() + "_" + originalFilename;
            Path targetPath = Paths.get(uploadDir).resolve(filename).normalize();

            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Image saved: {}", targetPath.toAbsolutePath());
            return filename;
        } catch (IOException ex) {
            log.error("Failed to save image", ex);
            throw new BusinessException("Failed to save image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Path loadImage(String filename) {
        Path path = Paths.get(uploadDir).resolve(filename).normalize();

        if (!Files.exists(path)) {
            log.warn("Requested image not found: {}", filename);
            throw new ResourceNotFoundException("Image with filename '" + filename + "' not found");
        }

        log.info("Loading image: {}", path.toAbsolutePath());
        return path;
    }
}
