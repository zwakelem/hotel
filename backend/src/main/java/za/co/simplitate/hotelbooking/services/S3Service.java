package za.co.simplitate.hotelbooking.services;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadFile(MultipartFile file);
}
