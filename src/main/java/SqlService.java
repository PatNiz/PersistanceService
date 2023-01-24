import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class SqlService {
    public void createTable(Class objectClass) throws SQLException {
        String className = objectClass.getSimpleName();
        Field[] fields = objectClass.getDeclaredFields();
        StringBuilder sb= new StringBuilder("DROP TABLE IF EXISTS "+className+";");
        sb.append(" CREATE TABLE " + className + "(");
        for (Field field : fields) {
            String fieldName = field.getName();
            Class fieldType = field.getType();
            String fieldTypeInDatabase = mapJavaTypeToDatabaseType(fieldType);
            sb.append(fieldName).append(" ").append(fieldTypeInDatabase).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length()).append(")");
        System.out.println(sb);
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sb.toString());
        }
    }
    public void dropTable(Class persistenceClass) throws SQLException {
        StringBuilder sb = new StringBuilder("DROP TABLE IF EXISTS ");
        sb.append(persistenceClass.getSimpleName());
        System.out.println(sb);
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sb.toString());
        }
    }
    protected static List<Persistable> read(Criteria criteria) throws SQLException {
        Connection connection = getConnection();
        StringBuilder sql = new StringBuilder("SELECT * FROM " + criteria.getClassType().getSimpleName() + " WHERE 1=1");
        for (Criterion criterion : criteria.getCriteria()) {
            sql.append(" AND ");
            sql.append(criterion.getColumnName());
            sql.append(" ");
            sql.append(criterion.getOperator().getSqlOperator());
            sql.append(" '");
            sql.append(criterion.getValue());
            sql.append("'");
        }
        PreparedStatement statement = connection.prepareStatement(sql.toString());
        ResultSet resultSet = statement.executeQuery();
        List<Persistable> persistableObjects = new ArrayList<>();
        while (resultSet.next()) {
            try {
                Constructor<?> constructor = criteria.getClassType().getConstructor();
                Persistable persistableObject = (Persistable) constructor.newInstance();
                for (Field field : criteria.getClassType().getDeclaredFields()) {
                    field.setAccessible(true);
                    field.set(persistableObject, resultSet.getObject(field.getName()));
                }
                persistableObjects.add(persistableObject);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return persistableObjects;
    }
    protected static void insert(Persistable persistableObject) {
        try (Connection conn = getConnection()) {
            String tableName = persistableObject.getClass().getSimpleName().toLowerCase();
            StringBuilder columnNames = new StringBuilder();
            StringBuilder values = new StringBuilder();
            for (Field field : persistableObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                columnNames.append(field.getName()).append(", ");
                if (field.getType() == String.class) {
                    values.append("'").append(field.get(persistableObject)).append("', ");
                } else {
                    values.append(field.get(persistableObject)).append(", ");
                }
            }
            columnNames.setLength(columnNames.length() - 2);
            values.setLength(values.length() - 2);
            String sql = "INSERT INTO " + tableName + " (" + columnNames + ") VALUES (" + values + ")";
            System.out.println(sql);
            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected static void delete(Persistable object) throws SQLException {
        String className = object.getClass().getSimpleName();
        Field[] fields = object.getClass().getDeclaredFields();
        StringBuilder query = new StringBuilder("DELETE FROM " + className + " WHERE ");
        for (Field field : fields) {
            String fieldName = field.getName();
            field.setAccessible(true);
            Object fieldValue;
            try {
                fieldValue = field.get(object);
            } catch (IllegalAccessException e) {
                throw new SQLException(e);
            }
            query.append(fieldName).append(" = ").append("'").append(fieldValue).append("'").append(" AND ");
        }
        query.delete(query.length() - 5, query.length());
        System.out.println(query);
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query.toString());
        }
    }
    public static void update(int id, Persistable object) throws SQLException {
        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(object.getClass().getSimpleName());
        sb.append(" SET ");
        Field[] declaredFields = object.getClass().getDeclaredFields();
        System.out.println("Update "+object.getClass().getSimpleName()+" with id="+id);
        System.out.println("Give new data");
        for (Field declaredField : declaredFields) {


            if(declaredField.getName()!="id") {
                sb.append(declaredField.getName());
                sb.append("=");
                System.out.println(declaredField.getName());
                if (declaredField.getType().getSimpleName().equals("String")) {
                    String updatedRow = new Scanner(System.in).nextLine();
                    sb.append("'");
                    sb.append(updatedRow);
                    sb.append("'");
                } else if (declaredField.getType().getSimpleName().equals("Long")) {
                    Long updatedRow = new Scanner(System.in).nextLong();
                    sb.append(updatedRow);
                } else if (declaredField.getType().getSimpleName().equals("Boolean")) {
                    Boolean updatedRow = new Scanner(System.in).nextBoolean();
                    sb.append(updatedRow);
                } else if (declaredField.getType().getSimpleName().equals("int")) {
                    Integer updatedRow = new Scanner(System.in).nextInt();
                    sb.append(updatedRow);
                }
                sb.append(",");
            }
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(" WHERE id =");
        sb.append(id);
        System.out.println(sb);
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.valueOf(sb));
        }
    }
    private String mapJavaTypeToDatabaseType(Class fieldType) {
        if (fieldType == int.class || fieldType == Integer.class) {
            return "INTEGER";
        } else if (fieldType == String.class) {
            return "VARCHAR(255)";
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            return "BOOLEAN";
        } else if (fieldType == float.class || fieldType == Float.class) {
            return "FLOAT";
        } else if (fieldType == double.class || fieldType == Double.class) {
            return "DOUBLE";
        }else if (fieldType == java.util.List.class) {
            return "JSONB";
        }
        else if (Persistable.class.isAssignableFrom(fieldType)) {
            return "JSONB";
        }else {
            throw new UnsupportedOperationException("Unsupported field type: " + fieldType);
        }
    }
    private static Connection getConnection() throws SQLException {
        Properties connectionProperties = new Properties();
        try (InputStream inputStream = SqlPersistenceService.class.getClassLoader().getResourceAsStream("database.properties")) {
            connectionProperties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String host = connectionProperties.getProperty("host");
        String port = connectionProperties.getProperty("port");
        String database = connectionProperties.getProperty("database");
        String username = connectionProperties.getProperty("username");
        String password = connectionProperties.getProperty("password");
        String connectionUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database + "?useSSL=false";
        return DriverManager.getConnection(connectionUrl, username, password);
    }
}
