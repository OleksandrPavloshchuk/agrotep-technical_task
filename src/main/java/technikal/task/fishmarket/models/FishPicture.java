package technikal.task.fishmarket.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "fish_picture")
public class FishPicture {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  @JoinColumn(name = "fish_id", nullable = false)
  private Fish fish;

  private String imageFileName;

  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }

  public String getImageFileName() {
    return imageFileName;
  }
  public void setImageFileName(String imageFileName) {
    this.imageFileName = imageFileName;
  }

  public Fish getFish() {
    return fish;
  }
  public void setFish(Fish fish) {
    this.fish = fish;
  }
}
