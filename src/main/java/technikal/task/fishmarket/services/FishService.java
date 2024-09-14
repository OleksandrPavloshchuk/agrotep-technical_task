package technikal.task.fishmarket.services;

import java.util.List;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishDto;

public interface FishService {

  List<Fish> getFishList();

  void deleteFish(int fishId);

  void addFish(FishDto fishDto);
}
