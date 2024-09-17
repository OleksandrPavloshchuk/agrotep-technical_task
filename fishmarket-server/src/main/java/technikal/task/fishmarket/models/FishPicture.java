package technikal.task.fishmarket.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Blob;

@Entity
@Table(name = "fish_picture")
public class FishPicture {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  @JoinColumn(name = "fish_id", nullable = false)
  private Fish fish;

  private String imageFileName;

  private String contentType;

  private long contentSize;

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public long getContentSize() {
    return contentSize;
  }

  public void setContentSize(long contentSize) {
    this.contentSize = contentSize;
  }

  public Blob getContent() {
    return content;
  }

  public void setContent(Blob content) {
    this.content = content;
  }

  @Lob
  private Blob content;

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
