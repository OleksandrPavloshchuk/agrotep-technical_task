package technikal.task.fishmarket.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.serial.SerialBlob;
import org.springframework.beans.factory.annotation.Autowired;
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

  private final FishRepository fishRepo;

  private final FishPictureRepository fishPictureRepo;

  @Autowired
  public FishServiceImpl(
      FishRepository fishRepo,
      FishPictureRepository fishPictureRepo) {
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
      fishPictureRepo.deleteAll(fish.getPictures());
      fishRepo.delete(fish);
    });
  }

  @Override
  public void addFish(FishDto fishDto) {
    Fish fish = new Fish();
    fish.setCatchDate(new Date());
    fish.setName(fishDto.getName());
    fish.setPrice(fishDto.getPrice());
    fish = fishRepo.save(fish);

    // Save the 1st fish picture:
    createFishPicture(fish, fishDto.getImageFile()).ifPresent(fishPictureRepo::save);
  }

  @Override
  public Optional<Fish> findFishById(int fishId) {
    return fishRepo.findById(fishId);
  }

  @Override
  public void addFishPicture(FishPictureDto fishPictureDto) {
    findFishById(fishPictureDto.getFishId())
        .flatMap(
            fish -> createFishPicture(fish, fishPictureDto.getImageFile()))
        .ifPresent(fishPictureRepo::save);
  }

  @Override
  public Optional<FishPicture> findFishPictureById(int fishPictureId) {
    return fishPictureRepo.findById(fishPictureId);
  }

  private Optional<FishPicture> createFishPicture(Fish fish, MultipartFile imageFile) {

    try {
      FishPicture result = new FishPicture();
      result.setFish(fish);
      result.setImageFileName(imageFile.getOriginalFilename());
      result.setContentType(imageFile.getContentType());
      result.setContentSize(imageFile.getSize());
      result.setContent(new SerialBlob(imageFile.getBytes()));
      return Optional.of(result);
    } catch (SQLException | IOException ex) {
      LOGGER.log(Level.SEVERE, "can't create fish picture", ex);
      return Optional.empty();
    }
  }

}
