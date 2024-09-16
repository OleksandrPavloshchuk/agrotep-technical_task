package technikal.task.fishmarket.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishDto;
import technikal.task.fishmarket.models.FishPicture;
import technikal.task.fishmarket.models.FishPictureDto;

@Service
public class FishServiceImpl implements FishService {

  private final FishRepository fishRepo;

  private final FishPictureRepository fishPictureRepo;

  private final ImageStorageService imageStorageService;

  @Autowired
  public FishServiceImpl(
      FishRepository fishRepo,
      FishPictureRepository fishPictureRepo,
      ImageStorageService imageStorageService) {
    this.fishRepo = fishRepo;
    this.fishPictureRepo = fishPictureRepo;
    this.imageStorageService = imageStorageService;
  }

  @Override
  public List<Fish> getFishList() {
    return fishRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
  }

  @Override
  public void deleteFish(int fishId) {
    fishRepo.findById(fishId).ifPresent(fish -> {
      deleteFishPictures(fish);
      fishPictureRepo.deleteAll(fish.getPictures());
      fishRepo.delete(fish);
    });
  }

  @Override
  public void addFish(FishDto fishDto) {
    final Date catchDate = new Date();
    imageStorageService.saveImage(fishDto.getImageFile(), catchDate).ifPresent(storedFileName -> {
      Fish fish = new Fish();
      fish.setCatchDate(catchDate);
      fish.setName(fishDto.getName());
      fish.setPrice(fishDto.getPrice());
      fish = fishRepo.save(fish);

      // Save the 1st fish picture:
      fishPictureRepo.save(new FishPicture(fish, storedFileName));
    });
  }

  @Override
  public Optional<Fish> findFishById(int fishId) {
    return fishRepo.findById(fishId);
  }

  @Override
  public void addFishPicture(FishPictureDto fishPictureDto) {
    findFishById(fishPictureDto.getFishId())
        .ifPresent(fish ->
            // Fish exists
            imageStorageService.saveImage(fishPictureDto.getImageFile(), new Date())
                .ifPresent(storedFileName ->
                    // Image file is successfully saved
                    fishPictureRepo.save(new FishPicture(fish, storedFileName))));
  }

  private void deleteFishPictures(Fish fish) {
    fish.getPictures().forEach(
        fishPicture -> imageStorageService.deleteImage(fishPicture.getImageFileName())
    );
  }

}
