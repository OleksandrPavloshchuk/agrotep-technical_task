package technikal.task.fishmarket.controllers;

import static technikal.task.fishmarket.controllers.ApplicationPage.LOGIN;
import static technikal.task.fishmarket.controllers.ApplicationPage.REDIRECT_TO_LIST_OF_FISHES;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

  @GetMapping("/login")
  public String login() {
    return LOGIN.getValue();
  }

  @GetMapping
  public String redirectToFishList() {
    return REDIRECT_TO_LIST_OF_FISHES.getValue();
  }
}
