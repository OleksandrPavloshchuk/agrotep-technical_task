package technikal.task.fishmarket.services;

import java.util.List;
import java.util.Optional;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishDto;
import technikal.task.fishmarket.models.FishPictureDto;

public interface FishService {

  List<Fish> getFishList();

  void deleteFish(int fishId);

  void addFish(FishDto fishDto);

  Optional<Fish> findFishById(int fishId);

  void addFishPicture(FishPictureDto fishPictureDto);
}
