package technikal.task.fishmarket.services;

import org.springframework.data.jpa.repository.JpaRepository;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishPicture;

public interface FishPictureRepository extends JpaRepository<FishPicture, Integer> {


}
