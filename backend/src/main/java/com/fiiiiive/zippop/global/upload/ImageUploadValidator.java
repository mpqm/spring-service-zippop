package com.fiiiiive.zippop.global.upload;

import com.fiiiiive.zippop.global.base.ServerErrorCode;
import com.fiiiiive.zippop.global.base.ServerException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

@Component
public class ImageUploadValidator {
    static final long MAX_BYTES = 10L * 1024 * 1024;
    static final long MAX_PIXELS = 25_000_000L;
    private static final Map<String, ValidatedImage> ALLOWED = Map.of(
            "JPEG", new ValidatedImage("jpg", "image/jpeg"),
            "JPG", new ValidatedImage("jpg", "image/jpeg"),
            "PNG", new ValidatedImage("png", "image/png"),
            "GIF", new ValidatedImage("gif", "image/gif")
    );

    public ValidatedImage validate(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() <= 0 || file.getSize() > MAX_BYTES) {
            throw invalidImage();
        }
        try (ImageInputStream input = ImageIO.createImageInputStream(file.getInputStream())) {
            if (input == null) throw invalidImage();
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw invalidImage();
            ImageReader reader = readers.next();
            try {
                ValidatedImage type = ALLOWED.get(reader.getFormatName().toUpperCase(Locale.ROOT));
                if (type == null) throw invalidImage();
                reader.setInput(input, true, true);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width <= 0 || height <= 0 || Math.multiplyExact((long) width, (long) height) > MAX_PIXELS) {
                    throw invalidImage();
                }
                BufferedImage decoded = reader.read(0);
                if (decoded == null) throw invalidImage();
                return type;
            } finally {
                reader.dispose();
            }
        } catch (IOException | ArithmeticException e) {
            throw invalidImage(e);
        }
    }

    private ServerException invalidImage() { return invalidImage(null); }
    private ServerException invalidImage(Throwable cause) {
        return new ServerException(ServerErrorCode.INVALID_IMAGE_FILE, cause);
    }

    public record ValidatedImage(String extension, String mediaType) {}
}
