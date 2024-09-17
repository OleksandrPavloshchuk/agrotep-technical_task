package technikal.task.fishmarket.controllers;

import static technikal.task.fishmarket.controllers.ApplicationPage.FORM_ADD_PICTURE;
import static technikal.task.fishmarket.controllers.ApplicationPage.FORM_CREATE_FISH;
import static technikal.task.fishmarket.controllers.ApplicationPage.LIST_OF_FISHES;
import static technikal.task.fishmarket.controllers.ApplicationPage.REDIRECT_TO_LIST_OF_FISHES;

import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishDto;
import technikal.task.fishmarket.models.FishPicture;
import technikal.task.fishmarket.models.FishPictureDto;
import technikal.task.fishmarket.services.BlobToInputStreamConvertor;
import technikal.task.fishmarket.services.FishService;

@Controller
@RequestMapping("/fish")
public class FishController {

  private static final String MODEL_ATTR_FISH_DTO = "fishDto";
  private static final String MODEL_ATTR_FISH_PICTURE_DTO = "fishPictureDto";

  private final FishService fishService;

  @Autowired
  public FishController(FishService fishService) {
    this.fishService = fishService;
  }

  @GetMapping
  public String showFishList(Model model) {
    model.addAttribute("fishlist", fishService.getFishList());
    return LIST_OF_FISHES.getValue();
  }

  @GetMapping("/create")
  public String showCreatePage(Model model) {
    model.addAttribute(MODEL_ATTR_FISH_DTO, new FishDto());
    return FORM_CREATE_FISH.getValue();
  }

  @GetMapping("/delete")
  public String deleteFish(@RequestParam int id) {
    fishService.deleteFish(id);
    return REDIRECT_TO_LIST_OF_FISHES.getValue();
  }

  @PostMapping("/create")
  public String addFish(@Valid @ModelAttribute FishDto fishDto, BindingResult result) {
    if (isFileEmpty(fishDto.getImageFile(), result)) {
      return FORM_CREATE_FISH.getValue();
    }
    fishService.addFish(fishDto);
    return REDIRECT_TO_LIST_OF_FISHES.getValue();
  }

  @GetMapping("/addPicture")
  public String showAddPicturePage(@RequestParam int fishId, Model model) {
    Optional<Fish> fishOpt = fishService.findFishById(fishId);
    if (fishOpt.isPresent()) {
      model.addAttribute(MODEL_ATTR_FISH_PICTURE_DTO,
          new FishPictureDto(fishId, fishOpt.get().getName()));
      return FORM_ADD_PICTURE.getValue();
    } else {
      return REDIRECT_TO_LIST_OF_FISHES.getValue();
    }
  }

  @PostMapping("/addPicture")
  public String addPicture(@Valid @ModelAttribute FishPictureDto fishPictureDto,
      BindingResult result) {
    if (isFileEmpty(fishPictureDto.getImageFile(), result)) {
      return FORM_ADD_PICTURE.getValue();
    }
    fishService.addFishPicture(fishPictureDto);
    return REDIRECT_TO_LIST_OF_FISHES.getValue();
  }

  @GetMapping("/picture")
  public ResponseEntity<?> getPicture(@RequestParam int id) {
    return fishService.findFishPictureById(id)
        .map(this::toResponseEntity)
        .orElse(ResponseEntity.notFound().build());
  }

  private boolean isFileEmpty(MultipartFile imageFile, BindingResult result) {
    if (imageFile.isEmpty()) {
      result.addError(
          new FieldError(MODEL_ATTR_FISH_PICTURE_DTO, "imageFile", "Потрібне фото рибки"));
    }
    return result.hasErrors();
  }

  private ResponseEntity<?> toResponseEntity(FishPicture fishPicture) {
    return new BlobToInputStreamConvertor(fishPicture.getContent()).toInputStream()
        .map(InputStreamResource::new)
        .map(inputStream -> ResponseEntity.ok()
            .contentLength(fishPicture.getContentSize())
            .header("Content-Type", fishPicture.getContentType())
            .body(inputStream))
        .orElse(ResponseEntity.noContent().build());
  }

}
