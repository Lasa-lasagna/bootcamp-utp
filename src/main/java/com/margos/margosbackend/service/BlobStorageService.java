package com.margos.margosbackend.service;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BlobStorageService {

    private final String connectionString;
    private final String contenedor;
    private volatile BlobServiceClient client;

    public BlobStorageService(
        @Value("${app.azure.storage.connection-string:}") String connectionString,
        @Value("${app.azure.storage.container:inspecciones-fotos}") String contenedor
    ) {
        this.connectionString = connectionString;
        this.contenedor = contenedor;
    }

    public String subir(MultipartFile archivo, Long inspeccionId) {
        if (connectionString == null || connectionString.isBlank()) {
            throw new IllegalStateException("Azure Blob Storage no configurado");
        }

        try {
            BlobServiceClient serviceClient = client();
            var containerClient = serviceClient.getBlobContainerClient(contenedor);
            if (!containerClient.exists()) {
                containerClient.create();
            }

            String nombreLimpio = limpiarNombre(archivo.getOriginalFilename());
            String blobName = "inspeccion-" + inspeccionId + "/" + UUID.randomUUID() + "-" + nombreLimpio;

            var blobClient = containerClient.getBlobClient(blobName);
            blobClient.upload(archivo.getInputStream(), archivo.getSize(), true);
            return blobClient.getBlobUrl();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el archivo", e);
        }
    }

    private BlobServiceClient client() {
        BlobServiceClient local = client;
        if (local == null) {
            synchronized (this) {
                local = client;
                if (local == null) {
                    local = new BlobServiceClientBuilder()
                        .connectionString(connectionString)
                        .buildClient();
                    client = local;
                }
            }
        }
        return local;
    }

    private String limpiarNombre(String original) {
        if (original == null || original.isBlank()) {
            return "archivo.bin";
        }
        return original.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
