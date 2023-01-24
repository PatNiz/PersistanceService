import java.io.IOException;
import java.sql.*;
import java.util.List;
public class SqlPersistenceService implements PersistenceService {
    @Override
    public List<Persistable> read(Criteria criteria) throws SQLException, IOException {
        List<Persistable> persistableObjects = SqlService.read(criteria);
        return persistableObjects;
    }
    public void insert(Persistable persistableObject) {
        SqlService.insert(persistableObject);
    }
    public void delete(Persistable persistableObject) throws SQLException {
        SqlService.delete(persistableObject);
    }
    public void update(Integer id, Persistable persistableObject) throws SQLException {
        SqlService.update(id, persistableObject);
    }
}
