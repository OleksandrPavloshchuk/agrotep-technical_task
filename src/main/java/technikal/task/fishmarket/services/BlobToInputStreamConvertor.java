package technikal.task.fishmarket.services;

import jakarta.validation.constraints.NotNull;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlobToInputStreamConvertor {

  private static final Logger LOGGER = Logger.getAnonymousLogger();

  private final Blob src;

  public BlobToInputStreamConvertor(@NotNull Blob src) {
    this.src = src;
  }

  public Optional<InputStream> toInputStream() {
    try {
      return Optional.of(src.getBinaryStream());
    } catch (SQLException ex) {
      LOGGER.log(Level.SEVERE, "can't get input stream from BLOB", ex);
      return Optional.empty();
    }
  }

}
