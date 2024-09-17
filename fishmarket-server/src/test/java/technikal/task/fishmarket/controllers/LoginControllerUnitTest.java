package technikal.task.fishmarket.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoginControllerUnitTest {

  @Test
  void login() {
    Assertions.assertEquals("login", new LoginController().login());
  }

  @Test
  void redirectToFishList() {
    Assertions.assertEquals("redirect:/fish", new LoginController().redirectToFishList());
  }
}
