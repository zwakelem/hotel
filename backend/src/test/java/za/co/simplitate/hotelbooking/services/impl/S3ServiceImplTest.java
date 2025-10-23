package za.co.simplitate.hotelbooking.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {

    @InjectMocks
    private S3ServiceImpl s3Service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(s3Service, "region", "eu-west-1");
        ReflectionTestUtils.setField(s3Service, "accessKey", "test-access-key");
        ReflectionTestUtils.setField(s3Service, "secretKey", "test-secret-key");
        ReflectionTestUtils.setField(s3Service, "bucketUrl", "https://test-bucket.s3.eu-west-1.amazonaws.com/");
    }

    @Test
    @DisplayName("uploadFile - should reject non-image files")
    void testUploadFile_nonImageFile() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes()
        );

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> s3Service.uploadFile(file));
        assertEquals("Only image file allowed!!", thrown.getMessage());
    }

    @Test
    @DisplayName("uploadFile - should accept image files content type")
    void testUploadFile_imageFileValidation() {
        MultipartFile pngFile = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "test image content".getBytes()
        );
        
        MultipartFile gifFile = new MockMultipartFile(
                "file",
                "test.gif",
                "image/gif",
                "test image content".getBytes()
        );

        // Test will attempt to connect to AWS and fail without valid credentials
        // but validates that the image content type check passes before AWS connection
        // We catch any exception, and if it's NOT "Only image file allowed", then the 
        // content type validation passed (which is what we want to test)
        try {
            s3Service.uploadFile(pngFile);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Only image file allowed!!")) {
                fail("PNG image file should be allowed but was rejected");
            }
            // Any other exception means validation passed but AWS connection failed (expected)
        } catch (Exception e) {
            // AWS SDK exceptions are expected without valid credentials
        }
        
        try {
            s3Service.uploadFile(gifFile);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Only image file allowed!!")) {
                fail("GIF image file should be allowed but was rejected");
            }
            // Any other exception means validation passed but AWS connection failed (expected)
        } catch (Exception e) {
            // AWS SDK exceptions are expected without valid credentials
        }
    }
}
