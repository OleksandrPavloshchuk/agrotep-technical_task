package technikal.task.fishmarket.controllers;

public enum ApplicationPage {
  LOGIN("login"),
  LIST_OF_FISHES("index"),
  FORM_CREATE_FISH("createFish"),
  FORM_ADD_PICTURE("addPicture"),
  REDIRECT_TO_LIST_OF_FISHES("redirect:/fish");
  private final String value;

  ApplicationPage(String name) {
    this.value = name;
  }

  public String getValue() {
    return value;
  }
}
