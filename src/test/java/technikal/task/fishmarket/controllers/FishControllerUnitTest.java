package technikal.task.fishmarket.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishDto;
import technikal.task.fishmarket.models.FishPictureDto;
import technikal.task.fishmarket.services.FishService;

class FishControllerUnitTest {

  @Mock
  private FishService fishService;

  @Mock
  private Model model;

  @Mock
  private MultipartFile imageFile;

  @Mock
  private BindingResult bindingResult;

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
  void showFishList() {
    FishController fishController = new FishController(fishService);
    Assertions.assertEquals("index", fishController.showFishList(model));
    verify(model).addAttribute(eq("fishlist"), any(List.class));
    verify(fishService).getFishList();
  }

  @Test
  void showCreatePage() {
    FishController fishController = new FishController(fishService);
    Assertions.assertEquals("createFish", fishController.showCreatePage(model));
    verify(model).addAttribute(eq("fishDto"), any(FishDto.class));
  }

  @Test
  void deleteFish() {
    FishController fishController = new FishController(fishService);
    Assertions.assertEquals("redirect:/fish", fishController.deleteFish(-42));
    ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
    verify(fishService).deleteFish(idCaptor.capture());
    Assertions.assertEquals(-42, idCaptor.getValue());
  }

  @Test
  void createFishOK() {
    FishController fishController = new FishController(fishService);
    FishDto fishDto = new FishDto();
    fishDto.setName("jellyfish");
    fishDto.setPrice(67.89);
    fishDto.setImageFile(imageFile);

    Assertions.assertEquals("redirect:/fish", fishController.addFish(fishDto, bindingResult));
    ArgumentCaptor<FishDto> captor = ArgumentCaptor.forClass(FishDto.class);
    verify(bindingResult, never()).addError(any(FieldError.class));
    verify(fishService).addFish(captor.capture());
    FishDto captured = captor.getValue();
    Assertions.assertNotNull(captured);
    Assertions.assertEquals("jellyfish", captured.getName());
    Assertions.assertEquals(67.89, captured.getPrice());
    Assertions.assertEquals(imageFile, captured.getImageFile());
  }

  @Test
  void createFishNoImage() {
    doReturn(true).when(imageFile).isEmpty();
    FishController fishController = new FishController(fishService);
    FishDto fishDto = new FishDto();
    fishDto.setName("other fish");
    fishDto.setPrice(0.01);
    fishDto.setImageFile(imageFile);
    BindingResult realBindingResult = new DirectFieldBindingResult(fishDto, "fishDto");
    Assertions.assertEquals("createFish", fishController.addFish(fishDto, realBindingResult));
    Assertions.assertEquals(1, realBindingResult.getErrorCount());
    ObjectError actualError = realBindingResult.getAllErrors().get(0);
    Assertions.assertInstanceOf(FieldError.class, actualError);
    FieldError actualFieldError = (FieldError) actualError;
    Assertions.assertEquals( "imageFile", actualFieldError.getField());
    Assertions.assertEquals( "Потрібне фото рибки", actualFieldError.getDefaultMessage());
  }

  @Test
  void showAddPicturePage() {
    Fish fish = new Fish();
    fish.setName("moonfish");
    doReturn(Optional.of(fish)).when(fishService).findFishById(any(Integer.class));
    FishController fishController = new FishController(fishService);
    Assertions.assertEquals("addPicture", fishController.showAddPicturePage(91, model));
    ArgumentCaptor<FishPictureDto> captor = ArgumentCaptor.forClass(FishPictureDto.class);
    verify(model).addAttribute(eq("fishPictureDto"), captor.capture());
    FishPictureDto actual = captor.getValue();
    Assertions.assertNotNull(actual);
    Assertions.assertEquals( 91, actual.getFishId());
    Assertions.assertEquals( "moonfish", actual.getFishName());
  }

  @Test
  void addPictureOK() {
    FishController fishController = new FishController(fishService);
    FishPictureDto fishPictureDto = new FishPictureDto();
    fishPictureDto.setFishId(100);
    fishPictureDto.setImageFile(imageFile);

    Assertions.assertEquals("redirect:/fish", fishController.addPicture(fishPictureDto, bindingResult));
    ArgumentCaptor<FishPictureDto> captor = ArgumentCaptor.forClass(FishPictureDto.class);
    verify(bindingResult, never()).addError(any(FieldError.class));
    verify(fishService).addFishPicture(captor.capture());
    FishPictureDto captured = captor.getValue();
    Assertions.assertNotNull(captured);
    Assertions.assertEquals(100, captured.getFishId());
    Assertions.assertEquals(imageFile, captured.getImageFile());
  }

  @Test
  void addPictireNoImage() {
    doReturn(true).when(imageFile).isEmpty();
    FishController fishController = new FishController(fishService);
    FishPictureDto fishPictureDto = new FishPictureDto();
    fishPictureDto.setFishId(100);
    fishPictureDto.setImageFile(imageFile);
    BindingResult realBindingResult = new DirectFieldBindingResult(fishPictureDto, "fishPictureDto");
    Assertions.assertEquals("addPicture", fishController.addPicture(fishPictureDto, realBindingResult));
    Assertions.assertEquals(1, realBindingResult.getErrorCount());
    ObjectError actualError = realBindingResult.getAllErrors().get(0);
    Assertions.assertInstanceOf(FieldError.class, actualError);
    FieldError actualFieldError = (FieldError) actualError;
    Assertions.assertEquals( "imageFile", actualFieldError.getField());
    Assertions.assertEquals( "Потрібне фото рибки", actualFieldError.getDefaultMessage());
  }
}
