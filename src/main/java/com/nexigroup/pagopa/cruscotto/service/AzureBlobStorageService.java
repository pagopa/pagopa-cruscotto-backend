package com.nexigroup.pagopa.cruscotto.service;

import com.azure.storage.blob.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for uploading and downloading files to/from Azure Blob Storage.
 */
@Service
public class AzureBlobStorageService {
    private static final Logger log = LoggerFactory.getLogger(AzureBlobStorageService.class);
    private final BlobContainerClient containerClient;

    /**
     * Initializes the AzureBlobStorageService using properties:
     * azure.blob.connection-string and azure.blob.container-name.
     *
     * @param connectionString The Azure Blob Storage connection string
     * @param containerName The Azure Blob Storage container name
     */
    public AzureBlobStorageService(
        @Value("${azure.blob.connection-string}") String connectionString,
        @Value("${azure.blob.container-name}") String containerName
    ) {
        Objects.requireNonNull(connectionString, "azure.blob.connection-string property is required");
        Objects.requireNonNull(containerName, "azure.blob.container-name property is required");
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient();
        this.containerClient = blobServiceClient.getBlobContainerClient(containerName);
    }

    /**
     * Uploads a file to Azure Blob Storage.
     *
     * @param data      The file content as a byte array.
     * @param blobPath  The path (including folders) where the file will be stored in the container.
     * @param fileName  The name of the file to be stored.
     * @throws IllegalArgumentException if data is null or empty.
     * @throws RuntimeException if upload fails.
     */
    public void uploadFile(byte[] data, String blobPath, String fileName) {
        if(data == null || data.length == 0) {
            throw new IllegalArgumentException("File data cannot be null or empty");
        }
        String fullPath = blobPath.endsWith("/") ? blobPath + fileName : blobPath + "/" + fileName;
        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            BlobClient blobClient = containerClient.getBlobClient(fullPath);
            blobClient.upload(inputStream, data.length, true);
            log.info("File uploaded to Azure Blob Storage at path: {}", fullPath);
        } catch (Exception e) {
            log.error("Failed to upload file to Azure Blob Storage", e);
            throw new RuntimeException("Failed to upload file to Azure Blob Storage", e);
        }
    }

    /**
     * Downloads a file from Azure Blob Storage.
     *
     * @param blobPath  The path (including folders) where the file is stored in the container.
     * @param fileName  The name of the file to be downloaded.
     * @return The file content as a byte array.
     * @throws IllegalArgumentException if blobPath or fileName is null or empty.
     * @throws RuntimeException if download fails or file does not exist.
     */
    public byte[] download(String blobPath, String fileName) {
        if(blobPath == null || blobPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Blob path cannot be null or empty");
        }
        if(fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        String fullPath = blobPath.endsWith("/") ? blobPath + fileName : blobPath + "/" + fileName;
        try {
            BlobClient blobClient = containerClient.getBlobClient(fullPath);

            if (!blobClient.exists()) {
                log.error("File not found in Azure Blob Storage at path: {}", fullPath);
                throw new RuntimeException("File not found: " + fullPath);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            blobClient.downloadStream(outputStream);
            byte[] data = outputStream.toByteArray();

            log.info("File downloaded from Azure Blob Storage at path: {}, size: {} bytes", fullPath, data.length);
            return data;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to download file from Azure Blob Storage", e);
            throw new RuntimeException("Failed to download file from Azure Blob Storage", e);
        }
    }

    /**
     * Uploads a file to Azure Blob Storage from an InputStream.
     *
     * @param inputStream The InputStream of the file.
     * @param blobPath    The path (including folders) where the file will be stored in the container.
     * @param fileName    The name of the file to be stored.
     * @throws RuntimeException if upload fails.
     */
    // public void upload(InputStream inputStream, String blobPath, String fileName) {
    //     // Implementation here
    // }

    /**
     * Uploads a file to Azure Blob Storage from a File object.
     *
     * @param file     The File to upload.
     * @param blobPath The path (including folders) where the file will be stored in the container.
     * @param fileName The name of the file to be stored.
     * @throws RuntimeException if upload fails.
     */
    // public void upload(File file, String blobPath, String fileName) {
    //     // Implementation here
    // }
}
