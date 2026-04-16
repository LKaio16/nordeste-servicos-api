package com.codagis.nordeste_servicos.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@ConditionalOnBean(Storage.class)
public class GoogleCloudStorageService {

    private final Storage storage;
    private final String bucketName;

    @Autowired
    public GoogleCloudStorageService(Storage storage, @Value("${gcloud.bucket:ne-servicos}") String bucketName) {
        this.storage = storage;
        this.bucketName = bucketName;
    }

    public String uploadImage(Long ordemServicoId, byte[] imageBytes, String contentType, String originalFilename) {
        String extension = "jpg";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            if (!extension.matches("jpg|jpeg|png|gif|webp")) {
                extension = "jpg";
            }
        }
        String objectName = "fotos-os/" + ordemServicoId + "/" + UUID.randomUUID() + "." + extension;

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType != null ? contentType : "image/jpeg")
                .build();

        storage.create(blobInfo, imageBytes);

        return "https://storage.googleapis.com/" + bucketName + "/" + objectName;
    }

    public String uploadImageForUsuario(Long usuarioId, byte[] imageBytes, String contentType, String originalFilename) {
        String extension = "jpg";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            if (!extension.matches("jpg|jpeg|png|gif|webp")) {
                extension = "jpg";
            }
        }
        String objectName = "fotos-usuarios/" + usuarioId + "/avatar." + extension;

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType != null ? contentType : "image/jpeg")
                .build();

        storage.create(blobInfo, imageBytes);

        return "https://storage.googleapis.com/" + bucketName + "/" + objectName;
    }

    public void deleteImage(String fotoUrl) {
        if (fotoUrl == null || !fotoUrl.startsWith("https://storage.googleapis.com/")) {
            return;
        }
        String path = fotoUrl.substring("https://storage.googleapis.com/".length());
        int slash = path.indexOf('/');
        if (slash <= 0) {
            return;
        }
        String bucket = path.substring(0, slash);
        String objectName = path.substring(slash + 1);
        storage.delete(BlobId.of(bucket, objectName));
    }
}
