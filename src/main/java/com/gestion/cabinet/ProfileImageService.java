package com.gestion.cabinet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProfileImageService {

	private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
	private final Path uploadDirectory = Path.of("data", "profile-images").toAbsolutePath().normalize();

	public String save(MultipartFile image) throws IOException {
		if (image == null || image.isEmpty()) {
			return null;
		}
		String contentType = image.getContentType();
		if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
			throw new IllegalArgumentException("Le fichier doit etre une image.");
		}
		String extension = extensionOf(image.getOriginalFilename());
		if (!ALLOWED_EXTENSIONS.contains(extension)) {
			throw new IllegalArgumentException("Formats acceptes: JPG, PNG, GIF ou WEBP.");
		}

		Files.createDirectories(uploadDirectory);
		String filename = UUID.randomUUID() + "." + extension;
		Path target = uploadDirectory.resolve(filename).normalize();
		if (!target.startsWith(uploadDirectory)) {
			throw new IOException("Chemin image invalide.");
		}
		image.transferTo(target);
		return filename;
	}

	public Path resolve(String filename) {
		return uploadDirectory.resolve(filename).normalize();
	}

	public Path uploadDirectory() {
		return uploadDirectory;
	}

	private String extensionOf(String filename) {
		if (filename == null || !filename.contains(".")) {
			return "";
		}
		return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
	}
}
