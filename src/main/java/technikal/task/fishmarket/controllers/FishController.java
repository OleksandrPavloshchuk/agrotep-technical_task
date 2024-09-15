package technikal.task.fishmarket.controllers;


import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishDto;
import technikal.task.fishmarket.models.FishPictureDto;
import technikal.task.fishmarket.services.FishService;

@Controller
@RequestMapping("/fish")
public class FishController {

  private static final String FISH_LIST = "index";
  private static final String REDIRECT_TO_FISH_LIST = "redirect:/fish";
  private static final String CREATE_FISH_FORM = "createFish";
  private static final String ADD_PICTURE_FORM = "addPicture";
  public static final String MODEL_ATTR_FISH_DTO = "fishDto";
  public static final String MODEL_ATTR_FISH_PICTURE_DTO = "fishPictureDto";

  private final FishService fishService;

  @Autowired
  public FishController(FishService fishService) {
    this.fishService = fishService;
  }

  @GetMapping
  public String showFishList(Model model) {
    model.addAttribute("fishlist", fishService.getFishList());
    return FISH_LIST;
  }

  @GetMapping("/create")
  public String showCreatePage(Model model) {
    model.addAttribute(MODEL_ATTR_FISH_DTO, new FishDto());
    return CREATE_FISH_FORM;
  }

  @GetMapping("/delete")
  public String deleteFish(@RequestParam int id) {
    fishService.deleteFish(id);
    return REDIRECT_TO_FISH_LIST;
  }

  @PostMapping("/create")
  public String addFish(@Valid @ModelAttribute FishDto fishDto, BindingResult result) {

    if (fishDto.getImageFile().isEmpty()) {
      result.addError(new FieldError(MODEL_ATTR_FISH_DTO, "imageFile", "Потрібне фото рибки"));
    }

    if (result.hasErrors()) {
      return CREATE_FISH_FORM;
    }
    fishService.addFish(fishDto);
    return REDIRECT_TO_FISH_LIST;
  }

  @GetMapping("/addPicture")
  public String showAddPicturePage(@RequestParam int fishId, Model model) {
    Optional<Fish> fishOpt = fishService.findFishById(fishId);
    if (fishOpt.isPresent()) {
      FishPictureDto fishPictureDto = new FishPictureDto();
      fishPictureDto.setFishId(fishId);
      fishPictureDto.setFishName(fishOpt.get().getName());
      model.addAttribute(MODEL_ATTR_FISH_PICTURE_DTO, fishPictureDto);
      return ADD_PICTURE_FORM;
    } else {
      return REDIRECT_TO_FISH_LIST;
    }
  }

  @PostMapping("/addPicture")
  public String addPicture(@Valid @ModelAttribute FishPictureDto fishPictureDto, BindingResult result) {

    if (fishPictureDto.getImageFile().isEmpty()) {
      result.addError(new FieldError(MODEL_ATTR_FISH_PICTURE_DTO, "imageFile", "Потрібне фото рибки"));
    }

    if (result.hasErrors()) {
      return ADD_PICTURE_FORM;
    }
    fishService.addFishPicture(fishPictureDto);
    return REDIRECT_TO_FISH_LIST;
  }

}
