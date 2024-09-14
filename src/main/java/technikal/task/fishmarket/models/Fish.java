package technikal.task.fishmarket.models;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "fish")
public class Fish {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String name;
  private double price;
  private Date catchDate;

  @OneToMany(mappedBy = "fish")
  private List<FishPicture> pictures;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public Date getCatchDate() {
    return catchDate;
  }

  public void setCatchDate(Date catchDate) {
    this.catchDate = catchDate;
  }

  public List<FishPicture> getPictures() {
    return pictures;
  }

  public void setPictures(List<FishPicture> pictures) {
    this.pictures = pictures;
  }
}
