package gm.proyecto.curso.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public FileStorageService() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio para guardar los archivos.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // --- ¡LA CORRECCIÓN ESTÁ AQUÍ! ---
        // 1. Obtenemos el nombre original del archivo.
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        // 2. Reemplazamos los espacios con guiones bajos para hacerlo seguro para URLs.
        String safeFileName = originalFileName.replace(" ", "_");

        // 3. Creamos el nombre de archivo único con el nombre ya limpio.
        String fileName = UUID.randomUUID().toString() + "_" + safeFileName;

        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("El nombre del archivo contiene una secuencia inválida.");
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);

            // Devolvemos la ruta de acceso al archivo
            return "/uploads/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo guardar el archivo " + fileName, ex);
        }
    }
}
