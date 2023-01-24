import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface PersistenceService {
    List<Persistable> read(Criteria criteria) throws SQLException, IOException;

}
