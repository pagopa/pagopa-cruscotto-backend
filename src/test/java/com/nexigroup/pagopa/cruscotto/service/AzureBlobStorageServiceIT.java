package com.nexigroup.pagopa.cruscotto.service;

import static org.assertj.core.api.Assertions.*;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.nexigroup.pagopa.cruscotto.IntegrationTest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Integration tests for {@link AzureBlobStorageService}.
 *
 * This test performs real uploads to Azure Blob Storage.
 * Make sure to configure valid Azure credentials before running this test.
 */
@IntegrationTest
class AzureBlobStorageServiceIT {

    private static final Logger log = LoggerFactory.getLogger(AzureBlobStorageServiceIT.class);

    @Autowired
    private AzureBlobStorageService azureBlobStorageService;

    @Value("${azure.blob.connection-string}")
    private String connectionString;

    @Value("${azure.blob.container-name}")
    private String containerName;

    private String testBlobPath;
    private String testFileName;

    @BeforeEach
    void setUp() {
        // Generate unique file name for each test to avoid conflicts
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
        testBlobPath = "test/uploads";
        testFileName = "test_file_" + timestamp + ".txt";
        log.info("Test setup - Will upload to: {}/{}", testBlobPath, testFileName);
    }

    @AfterEach
    void tearDown() {
        // Clean up - delete the test file after test execution
        try {
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            String fullPath = testBlobPath + "/" + testFileName;
            BlobClient blobClient = containerClient.getBlobClient(fullPath);

            if (blobClient.exists()) {
                blobClient.delete();
                log.info("Test cleanup - Deleted test file: {}", fullPath);
            }
        } catch (Exception e) {
            log.warn("Failed to clean up test file", e);
        }
    }

    @Test
    void testUploadByteArray() throws Exception {
        // Given
        String testContent = "This is a test file content - " + LocalDateTime.now();
        byte[] data = testContent.getBytes(StandardCharsets.UTF_8);

        // When
        assertThatCode(() ->
            azureBlobStorageService.uploadFile(data, testBlobPath, testFileName)
        ).doesNotThrowAnyException();

        // Then - Verify the file was uploaded successfully
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        String fullPath = testBlobPath + "/" + testFileName;
        BlobClient blobClient = containerClient.getBlobClient(fullPath);

        assertThat(blobClient.exists()).isTrue();
        assertThat(blobClient.getProperties().getBlobSize()).isEqualTo(data.length);

        log.info("Test passed - File successfully uploaded to Azure Blob Storage: {}", fullPath);
    }

    @Test
    void testUploadWithNullData() {
        // Given
        byte[] data = null;

        // When / Then
        assertThatThrownBy(() ->
            azureBlobStorageService.uploadFile(data, testBlobPath, testFileName)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testUploadWithEmptyData() {
        // Given
        byte[] data = new byte[0];

        // When
        assertThatThrownBy(() ->
            azureBlobStorageService.uploadFile(data, testBlobPath, testFileName)
        ).isInstanceOf(IllegalArgumentException.class);

        // Then - Verify empty file was created
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        String fullPath = testBlobPath + "/" + testFileName;
        BlobClient blobClient = containerClient.getBlobClient(fullPath);

        assertThat(blobClient.exists()).isFalse();
    }

    @Test
    void testUploadWithPathEndingWithSlash() {
        // Given
        String testContent = "Test content with slash in path";
        byte[] data = testContent.getBytes(StandardCharsets.UTF_8);
        String pathWithSlash = testBlobPath + "/";

        // When
        assertThatCode(() ->
            azureBlobStorageService.uploadFile(data, pathWithSlash, testFileName)
        ).doesNotThrowAnyException();

        // Then
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        String fullPath = testBlobPath + "/" + testFileName;
        BlobClient blobClient = containerClient.getBlobClient(fullPath);

        assertThat(blobClient.exists()).isTrue();
    }

    @Test
    void testUploadLargeFile() {
        // Given - Create a 1MB test file
        byte[] data = new byte[1024 * 1024]; // 1MB
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i % 256);
        }

        // When
        assertThatCode(() ->
            azureBlobStorageService.uploadFile(data, testBlobPath, testFileName)
        ).doesNotThrowAnyException();

        // Then
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        String fullPath = testBlobPath + "/" + testFileName;
        BlobClient blobClient = containerClient.getBlobClient(fullPath);

        assertThat(blobClient.exists()).isTrue();
        assertThat(blobClient.getProperties().getBlobSize()).isEqualTo(data.length);
    }

    @Test
    void testDownloadFile() throws Exception {
        // Given - Upload a test file first
        String testContent = "This is test content for download - " + LocalDateTime.now();
        byte[] uploadedData = testContent.getBytes(StandardCharsets.UTF_8);
        azureBlobStorageService.uploadFile(uploadedData, testBlobPath, testFileName);

        // When
        byte[] downloadedData = azureBlobStorageService.download(testBlobPath, testFileName);

        // Then
        assertThat(downloadedData).isNotNull();
        assertThat(downloadedData).isEqualTo(uploadedData);
        assertThat(new String(downloadedData, StandardCharsets.UTF_8)).isEqualTo(testContent);

        log.info("Test passed - File successfully downloaded from Azure Blob Storage");
    }

    @Test
    void testDownloadNonExistentFile() {
        // Given
        String nonExistentPath = "non/existent/path";
        String nonExistentFileName = "non_existent_file.txt";

        // When / Then
        assertThatThrownBy(() ->
            azureBlobStorageService.download(nonExistentPath, nonExistentFileName)
        )
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("File not found");
    }

    @Test
    void testDownloadWithNullPath() {
        // Given
        String nullPath = null;

        // When / Then
        assertThatThrownBy(() ->
            azureBlobStorageService.download(nullPath, testFileName)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testDownloadWithEmptyPath() {
        // Given
        String emptyPath = "";

        // When / Then
        assertThatThrownBy(() ->
            azureBlobStorageService.download(emptyPath, testFileName)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testDownloadWithNullFileName() {
        // Given
        String nullFileName = null;

        // When / Then
        assertThatThrownBy(() ->
            azureBlobStorageService.download(testBlobPath, nullFileName)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testDownloadWithEmptyFileName() {
        // Given
        String emptyFileName = "";

        // When / Then
        assertThatThrownBy(() ->
            azureBlobStorageService.download(testBlobPath, emptyFileName)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testDownloadLargeFile() throws Exception {
        // Given - Upload a large file (1MB)
        byte[] largeData = new byte[1024 * 1024]; // 1MB
        for (int i = 0; i < largeData.length; i++) {
            largeData[i] = (byte) (i % 256);
        }
        azureBlobStorageService.uploadFile(largeData, testBlobPath, testFileName);

        // When
        byte[] downloadedData = azureBlobStorageService.download(testBlobPath, testFileName);

        // Then
        assertThat(downloadedData).isNotNull();
        assertThat(downloadedData.length).isEqualTo(largeData.length);
        assertThat(downloadedData).isEqualTo(largeData);

        log.info("Test passed - Large file successfully downloaded, size: {} bytes", downloadedData.length);
    }
}
