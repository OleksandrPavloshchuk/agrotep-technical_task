package technikal.task.pictures.uploader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

  private static final Logger LOGGER = Logger.getAnonymousLogger();

  private final Properties props;

  public static void main(String[] args) {
    try {
      new Main().run();
    } catch (Exception ex) {
      LOGGER.log(Level.SEVERE, "can't upload pictures", ex);
    }
  }

  private Main() throws IOException, ClassNotFoundException {
    props = new Properties();
    Class.forName(props.getDbDriverClass());
  }

  public void run() throws SQLException {
    getNotUploadedImages().forEach(this::uploadImage);
  }

  private Map<Integer, String> getNotUploadedImages() throws SQLException {
    final Map<Integer, String> result = new HashMap<>();
    try (Connection connection = openConnection()) {
      try (Statement stmt = connection.createStatement()) {
        try (ResultSet rs = stmt.executeQuery(
            "select id, image_file_name from fish_picture where content is null")) {
          while (rs.next()) {
            result.put(rs.getInt(1), rs.getString(2));
          }
        }
      }
    }
    return result;
  }

  private void uploadImage(int id, String imageFileName) {
    LOGGER.log(Level.INFO, "Processing {} ...", imageFileName);
    getContent(imageFileName).ifPresent(content -> saveContent(id, content));
  }

  private void saveContent(int id, byte[] content) {
    try {
      try (Connection connection = openConnection()) {
        try (PreparedStatement stmt = connection.prepareStatement(
            "update fish_picture set content=? where id=?"
        )) {
          stmt.setInt(2, id);
          try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content)) {
            stmt.setBlob(1, byteArrayInputStream);
          }
          stmt.executeUpdate();
        }
      }
    } catch (SQLException | IOException ex) {
      LOGGER.log(Level.SEVERE, "can't save content", ex);
    }
  }

  private Optional<byte[]> getContent(String imageFileName) {
    try {
      String fileName = props.getFilesUploadDir() + "/" + imageFileName;
      Path filePath = Paths.get(fileName);
      try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
        Files.copy(filePath, byteArrayOutputStream);
        return Optional.of(byteArrayOutputStream.toByteArray());
      }
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, "can't read file " + imageFileName, ex);
      return Optional.empty();
    }
  }

  private Connection openConnection() throws SQLException {
    return DriverManager.getConnection(props.getDbUrl(),
        props.getDbUsername(), props.getDbPassword());
  }

}