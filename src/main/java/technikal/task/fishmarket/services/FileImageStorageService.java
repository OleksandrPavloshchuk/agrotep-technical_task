package technikal.task.fishmarket.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileImageStorageService implements ImageStorageService {

  private static final Logger LOGGER = Logger.getAnonymousLogger();

  @Value("${files.upload.dir}")
  private String filesUploadDir;

  @Override
  public Optional<String> saveImage(MultipartFile image, Date timestamp) {
    String storageFileName = timestamp.getTime() + "_" + image.getOriginalFilename();
    Path uploadPath = Paths.get(filesUploadDir);

    try {
      if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
      }

      try (InputStream inputStream = image.getInputStream()) {
        Files.copy(inputStream, Paths.get(filesUploadDir + "/" + storageFileName),
            StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "can't save file: {}", storageFileName);
      return Optional.empty();
    }

    return Optional.of(storageFileName);
  }

  @Override
  public void deleteImage(String imageName) {
    try {
      Path imagePath = Paths.get(filesUploadDir + "/" + imageName);
      Files.delete(imagePath);
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "can't delete file: {}", imageName);
    }
  }
}
