package com.laure.perfumeria.backend.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/archivos")
public class ImagenProductoController {

    private final Path uploadDirectory = Paths.get(System.getProperty("user.dir"), "uploads", "productos");

    @PostMapping(value = "/productos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> subirImagen(@RequestPart("archivo") MultipartFile archivo) throws IOException {
        if (archivo.isEmpty() || archivo.getContentType() == null || !archivo.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Selecciona una imagen válida."));
        }
        Files.createDirectories(uploadDirectory);
        String extension = extension(archivo.getOriginalFilename(), archivo.getContentType());
        String filename = UUID.randomUUID() + extension;
        Files.copy(archivo.getInputStream(), uploadDirectory.resolve(filename));
        return ResponseEntity.ok(Map.of("url", "/api/archivos/productos/" + filename));
    }

    @GetMapping("/productos/{filename:.+}")
    public ResponseEntity<Resource> verImagen(@PathVariable String filename) throws MalformedURLException {
        Path file = uploadDirectory.resolve(filename).normalize();
        if (!file.startsWith(uploadDirectory.normalize()) || !Files.exists(file)) return ResponseEntity.notFound().build();
        Resource resource = new UrlResource(file.toUri());
        String contentType = MediaTypeFactory.getMediaType(filename).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(resource);
    }

    private String extension(String filename, String contentType) {
        if (filename != null && filename.contains(".")) return filename.substring(filename.lastIndexOf('.')).toLowerCase();
        return switch (contentType) { case "image/png" -> ".png"; case "image/webp" -> ".webp"; default -> ".jpg"; };
    }
}
