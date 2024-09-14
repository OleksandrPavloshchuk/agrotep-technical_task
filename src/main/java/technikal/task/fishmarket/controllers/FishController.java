package technikal.task.fishmarket.controllers;


import jakarta.validation.Valid;
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
import technikal.task.fishmarket.models.FishDto;
import technikal.task.fishmarket.services.FishService;

@Controller
@RequestMapping("/fish")
public class FishController {

  private static final String FISH_LIST = "index";
  private static final String REDIRECT_TO_FISH_LIST = "redirect:/fish";
  private static final String CREATE_FISH_FORM = "createFish";

  @Autowired
  private FishService fishService;

  @GetMapping({"", "/"})
  public String showFishList(Model model) {
    model.addAttribute("fishlist", fishService.getFishList());
    return FISH_LIST;
  }

  @GetMapping("/create")
  public String showCreatePage(Model model) {
    model.addAttribute("fishDto", new FishDto());
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
      result.addError(new FieldError("fishDto", "imageFile", "Потрібне фото рибки"));
    }

    if (result.hasErrors()) {
      return CREATE_FISH_FORM;
    }
    fishService.addFish(fishDto);
    return REDIRECT_TO_FISH_LIST;
  }

}
