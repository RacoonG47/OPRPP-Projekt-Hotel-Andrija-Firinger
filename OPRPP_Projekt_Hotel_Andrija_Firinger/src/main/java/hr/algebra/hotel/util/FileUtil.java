package hr.algebra.hotel.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class FileUtil {

    private static final String ASSETS_DIR = "assets/";

    private FileUtil() {}

    public static String copyImageToAssets(File sourceFile) {
        try {
            Path assetsPath = Paths.get(ASSETS_DIR);
            if (!Files.exists(assetsPath)) {
                Files.createDirectories(assetsPath);
            }
            String fileName = System.currentTimeMillis() + "_" + sourceFile.getName();
            Path destination = assetsPath.resolve(fileName);
            Files.copy(sourceFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            return ASSETS_DIR + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy image to assets", e);
        }
    }

    public static void deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) return;
        try {
            Files.deleteIfExists(Paths.get(imagePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image", e);
        }
    }

    public static boolean imageExists(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) return false;
        return Files.exists(Paths.get(imagePath));
    }
}