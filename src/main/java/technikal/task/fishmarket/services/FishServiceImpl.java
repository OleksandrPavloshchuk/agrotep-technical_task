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
import technikal.task.fishmarket.models.FishPicture;
import technikal.task.fishmarket.models.FishPictureDto;

@Service
public class FishServiceImpl implements FishService {

  private static final Logger LOGGER = Logger.getAnonymousLogger();

  @Value("${files.upload.dir}")
  private String filesUploadDir;

  private final FishRepository fishRepo;

  private final FishPictureRepository fishPictureRepo;

  @Autowired
  public FishServiceImpl(FishRepository fishRepo, FishPictureRepository fishPictureRepo) {
    this.fishRepo = fishRepo;
    this.fishPictureRepo = fishPictureRepo;
  }

  @Override
  public List<Fish> getFishList() {
    return fishRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
  }

  @Override
  public void deleteFish(int fishId) {
    fishRepo.findById(fishId).ifPresent(fish -> {
      deleteFishPictures(fish);
      fish.getPictures().forEach(fishPictureRepo::delete);
      fishRepo.delete(fish);
    });
  }

  @Override
  public void addFish(FishDto fishDto) {
    final Date catchDate = new Date();
    saveFile(fishDto.getImageFile(), catchDate).ifPresent(storedFileName -> {
      // We save only the 1st fish image while creation:
      Fish fish = new Fish();
      fish.setCatchDate(catchDate);
      fish.setName(fishDto.getName());
      fish.setPrice(fishDto.getPrice());
      fish = fishRepo.save(fish);

      // Save the 1st fish picture:
      FishPicture fishPicture = createFishPicture(fish, storedFileName);
      fishPictureRepo.save(fishPicture);
    });
  }

  @Override
  public Optional<Fish> findFishById(int fishId) {
    return fishRepo.findById(fishId);
  }

  @Override
  public void addFishPicture(FishPictureDto fishPictureDto) {
    saveFile(fishPictureDto.getImageFile(), new Date()).ifPresent(storedFileName -> {
      findFishById(fishPictureDto.getFishId()).ifPresent(fish -> {
        FishPicture fishPicture = createFishPicture(fish, storedFileName);
        fishPictureRepo.save(fishPicture);
      });
    });
  }

  private FishPicture createFishPicture(Fish fish, String fileName) {
    FishPicture result = new FishPicture();
    result.setFish(fish);
    result.setImageFileName(fileName);
    return result;
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
      LOGGER.log(Level.SEVERE, "can't save file: {}", storageFileName);
      return Optional.empty();
    }

    return Optional.of(storageFileName);
  }

  private void deleteFishPictures(Fish fish) {
    fish.getPictures().forEach(
        fishPicture -> deleteFile(fishPicture.getImageFileName())
    );
  }

  private void deleteFile(String imageFileName) {
    try {
      Path imagePath = Paths.get(filesUploadDir + "/" + imageFileName);
      Files.delete(imagePath);
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "can't delete file: {}", imageFileName);
    }
  }

}
