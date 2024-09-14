package technikal.task.fishmarket.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishDto;

@Service
public class FishServiceImpl implements FishService {

  private static final Logger LOGGER = Logger.getAnonymousLogger();

  @Value("${files.upload.dir}")
  private String filesUploadDir;

  private final FishRepository repo;

  @Autowired
  public FishServiceImpl(FishRepository repo) {
    this.repo = repo;
  }

  @Override
  public List<Fish> getFishList() {
    return repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
  }

  @Override
  public void deleteFish(int fishId) {
    repo.findById(fishId).ifPresent(fish -> {
      deleteFile(fish.getImageFileName());
      repo.delete(fish);
    });
  }

  @Override
  public void addFish(FishDto fishDto) {
    final Date catchDate = new Date();
    saveFile(fishDto.getImageFile(), catchDate).ifPresent( storageFileName -> {
      Fish fish = new Fish();

      fish.setCatchDate(catchDate);
      fish.setImageFileName(storageFileName);
      fish.setName(fishDto.getName());
      fish.setPrice(fishDto.getPrice());

      repo.save(fish);
    });
  }

  // TODO create special service for file processing

  private Optional<String> saveFile(MultipartFile image, Date catchDate) {
    String storageFileName = catchDate.getTime() + "_" + image.getOriginalFilename();
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
      LOGGER.log(Level.SEVERE, "can't save file: " + storageFileName);
      return Optional.empty();
    }

    return Optional.of(storageFileName);
  }

  private void deleteFile(String imageFileName) {
    try {
      Path imagePath = Paths.get(filesUploadDir + "/" + imageFileName);
      Files.delete(imagePath);
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "can't delete file: " + imageFileName);
    }
  }

}
