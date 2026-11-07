package com.fiiiiive.zippop.global.upload;

import com.fiiiiive.zippop.global.base.ServerException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class ImageUploadSecurityTest {
    private final ImageUploadValidator validator = new ImageUploadValidator();

    @Test void rejectsTextEvenWhenFilenameAndContentTypeClaimPng() {
        var file = new MockMultipartFile("file", "image.png", "image/png", "<script>alert(1)</script>".getBytes());
        assertThatThrownBy(() -> validator.validate(file)).isInstanceOf(ServerException.class);
    }

    @Test void localUploadUsesOnlyServerGeneratedSafeFilenameAndCanDelete(@TempDir Path temp) throws Exception {
        LocalFileUploadService service = new LocalFileUploadService(validator);
        ReflectionTestUtils.setField(service, "uploadPath", temp.toString());
        var file = new MockMultipartFile("file", "../../escape.html", "text/html", png());

        String url = service.singleUpload(file);

        assertThat(url).matches("/uploads/[0-9a-f-]{36}\\.png");
        Path saved = temp.resolve(url.substring("/uploads/".length()));
        assertThat(saved).exists();
        assertThat(temp.getParent().resolve("escape.html")).doesNotExist();
        service.delete(url);
        assertThat(saved).doesNotExist();
    }

    @Test void multipleUploadRemovesAlreadyWrittenFilesWhenALaterFileIsInvalid(@TempDir Path temp) throws Exception {
        LocalFileUploadService service = new LocalFileUploadService(validator);
        ReflectionTestUtils.setField(service, "uploadPath", temp.toString());
        var good = new MockMultipartFile("files", "good.png", "image/png", png());
        var bad = new MockMultipartFile("files", "bad.png", "image/png", "not-image".getBytes());

        assertThatThrownBy(() -> service.multipleUpload(new MockMultipartFile[]{good, bad}))
                .isInstanceOf(ServerException.class);
        try (var files = Files.list(temp)) {
            assertThat(files).isEmpty();
        }
    }

    private byte[] png() throws Exception {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);
        return output.toByteArray();
    }
}
