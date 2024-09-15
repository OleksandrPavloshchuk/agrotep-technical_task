package technikal.task.fishmarket.models;

import org.springframework.web.multipart.MultipartFile;

public class FishPictureDto {

  private int fishId;
  private String fishName;
  private MultipartFile imageFile;

  public int getFishId() {
    return fishId;
  }

  public void setFishId(int fishId) {
    this.fishId = fishId;
  }

  public String getFishName() {
    return fishName;
  }

  public void setFishName(String fishName) {
    this.fishName = fishName;
  }

  public MultipartFile getImageFile() {
    return imageFile;
  }

  public void setImageFile(MultipartFile imageFile) {
    this.imageFile = imageFile;
  }

}
