package technikal.task.fishmarket.services;

import java.util.Date;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
  Optional<String> saveImage(MultipartFile image, Date timestamp);

  void deleteImage(String imageName);

}
