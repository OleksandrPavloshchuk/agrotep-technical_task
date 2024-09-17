package technikal.task.pictures.uploader;

import java.io.IOException;
import java.io.InputStream;

public class Properties {
  private final String dbDriverClass;
  private final String dbUrl;
  private final String dbUsername;
  private final String dbPassword;
  private final String filesUploadDir;

  public Properties() throws IOException {
    InputStream inputStream = getClass().getResourceAsStream("/application.properties");
    java.util.Properties props = new java.util.Properties();
    props.load(inputStream);
    dbDriverClass = props.getProperty("spring.datasource.driver-class-name");
    dbUrl = props.getProperty("spring.datasource.url");
    dbUsername = props.getProperty("spring.datasource.username");
    dbPassword = props.getProperty("spring.datasource.password");
    filesUploadDir = props.getProperty("files.upload.dir");
  }

  public String getDbDriverClass() {
    return dbDriverClass;
  }

  public String getDbUrl() {
    return dbUrl;
  }

  public String getDbUsername() {
    return dbUsername;
  }

  public String getDbPassword() {
    return dbPassword;
  }

  public String getFilesUploadDir() {
    return filesUploadDir;
  }
}
