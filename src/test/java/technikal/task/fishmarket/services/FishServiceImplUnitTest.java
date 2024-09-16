package technikal.task.fishmarket.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.multipart.MultipartFile;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishDto;
import technikal.task.fishmarket.models.FishPicture;
import technikal.task.fishmarket.models.FishPictureDto;

class FishServiceImplUnitTest {

  @Mock
  private FishRepository fishRepo;

  @Mock
  private FishPictureRepository fishPictureRepo;

  @Mock
  private ImageStorageService imageStorageService;

  @Mock
  private MultipartFile imageFile;

  private AutoCloseable autoCloseable;

  @BeforeEach
  public void setUp() {
    autoCloseable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  public void tearDown() throws Exception {
    autoCloseable.close();
  }

  @Test
  void getFishList() {
    FishService fishService = new FishServiceImpl(fishRepo, fishPictureRepo, imageStorageService);
    fishService.getFishList();
    ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
    verify(fishRepo).findAll(sortCaptor.capture());
    Sort sort = sortCaptor.getValue();
    Assertions.assertThat(sort).isEqualTo(Sort.by(Direction.DESC, "id"));
  }

  @Test
  public void findFishById() {
    FishService fishService = new FishServiceImpl(fishRepo, fishPictureRepo, imageStorageService);
    fishService.findFishById(7001);
    ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
    verify(fishRepo).findById(idCaptor.capture());
    Assertions.assertThat(7001).isEqualTo(idCaptor.getValue());
  }

  @Test
  void deleteFishIfFound() {
    Fish fish = new Fish();
    fish.setPictures(List.of(createPicture("1"), createPicture("2")));
    doReturn(Optional.of(fish)).when(fishRepo).findById(eq(4));
    FishService fishService = new FishServiceImpl(fishRepo, fishPictureRepo, imageStorageService);
    fishService.deleteFish(4);
    ArgumentCaptor<Fish> fishCaptor = ArgumentCaptor.forClass(Fish.class);
    verify(fishRepo).delete(fishCaptor.capture());
    Assertions.assertThat(fishCaptor.getValue()).isEqualTo(fish);
    verify(imageStorageService, times(2)).deleteImage(any(String.class));
    verify(fishPictureRepo).deleteAll(any(Iterable.class));
  }

  @Test
  void deleteFishIfNotFound() {
    doReturn(Optional.empty()).when(fishRepo).findById(eq(4));
    FishService fishService = new FishServiceImpl(fishRepo, fishPictureRepo, imageStorageService);
    fishService.deleteFish(100);
    verify(fishRepo, never()).delete(any(Fish.class));
    verify(imageStorageService, never()).deleteImage(any(String.class));
    verify(fishPictureRepo, never()).delete(any(FishPicture.class));
  }

  @Test
  void addFish() {
    doReturn(Optional.of("save image"))
        .when(imageStorageService)
        .saveImage(any(MultipartFile.class), any(Date.class));
    FishDto fishDto = new FishDto();
    fishDto.setImageFile(imageFile);
    fishDto.setPrice(8);
    fishDto.setName("some fish");
    FishService fishService = new FishServiceImpl(fishRepo, fishPictureRepo, imageStorageService);
    fishService.addFish(fishDto);
    ArgumentCaptor<Fish> fishCaptor = ArgumentCaptor.forClass(Fish.class);
    verify(fishRepo).save(fishCaptor.capture());
    Fish fish = fishCaptor.getValue();
    Assertions.assertThat(fish).isNotNull();
    Assertions.assertThat(fish.getName()).isEqualTo("some fish");
    Assertions.assertThat(fish.getPrice()).isEqualTo(8);

    ArgumentCaptor<FishPicture> fishPictureCaptor = ArgumentCaptor.forClass(FishPicture.class);
    verify(fishPictureRepo).save(fishPictureCaptor.capture());
    FishPicture fishPicture = fishPictureCaptor.getValue();
    Assertions.assertThat(fishPicture).isNotNull();
    Assertions.assertThat(fishPicture.getImageFileName()).isEqualTo("save image");
  }

  @Test
  void addFishPicture() {
    doReturn(Optional.of("save another image"))
        .when(imageStorageService)
        .saveImage(any(MultipartFile.class), any(Date.class));
    doReturn(Optional.of(new Fish())).when(fishRepo).findById(any(Integer.class));
    FishPictureDto fishPictureDto = new FishPictureDto();
    fishPictureDto.setImageFile(imageFile);
    fishPictureDto.setFishId(8910);
    FishService fishService = new FishServiceImpl(fishRepo, fishPictureRepo, imageStorageService);
    fishService.addFishPicture(fishPictureDto);
    verify(fishRepo, never()).save(any(Fish.class));

    ArgumentCaptor<FishPicture> fishPictureCaptor = ArgumentCaptor.forClass(FishPicture.class);
    verify(fishPictureRepo).save(fishPictureCaptor.capture());
    FishPicture fishPicture = fishPictureCaptor.getValue();
    Assertions.assertThat(fishPicture).isNotNull();
    Assertions.assertThat(fishPicture.getImageFileName()).isEqualTo("save another image");
  }

  private FishPicture createPicture(String name) {
    FishPicture result = new FishPicture();
    result.setImageFileName(name);
    return result;
  }
}
