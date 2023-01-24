import javax.print.Doc;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class app {
    public static void main(String[] args) throws SQLException, IOException {
        SqlPersistenceService sqlPersistenceService = new SqlPersistenceService();
        JsonPersistenceService persistenceService = new JsonPersistenceService();
        SqlService sqlService = new SqlService();
        Project proj1 = new Project(1, "proj1");
        Document doc1 = new Document(1,"doc1","content doc1");
        Document doc2 = new Document(2, "doc2","content doc2");
        Document doc3 = new Document(3,"doc3","content doc3");
        sqlService.createTable(Project.class);
        sqlService.createTable(Document.class);
        sqlPersistenceService.insert(proj1);
        sqlPersistenceService.insert(doc1);
        sqlPersistenceService.insert(doc2);
        sqlPersistenceService.insert(doc3);
        Criteria docCriteriaFromSql = new Criteria();
        docCriteriaFromSql.setClassType(Document.class);
        List<Persistable> documentsFromDatabase = sqlPersistenceService.read(docCriteriaFromSql);
        System.out.println("Documents from database: " + documentsFromDatabase);
        sqlPersistenceService.delete(doc3);
        List<Persistable> documentsWithoutDoc3FromDatabase = sqlPersistenceService.read(docCriteriaFromSql);
        System.out.println("Documents list after delete doc3: "+ documentsWithoutDoc3FromDatabase);
        sqlPersistenceService.update(2,new Document(2,"doc2","content changed"));
        List<Persistable> documentsWithUpdatedDoc2FromDatabase = sqlPersistenceService.read(docCriteriaFromSql);
        System.out.println("Documents with updated Doc2 From Database: "+ documentsWithUpdatedDoc2FromDatabase);
        Criterion docCriterion = new Criterion("name", Operator.Equals, "doc2");
        docCriteriaFromSql.addCriterion(docCriterion);
        List<Persistable> documentsWithFiltrFromDatabase = sqlPersistenceService.read(docCriteriaFromSql);
        System.out.println("Documents with filtr(\"name\"=\"doc2\") from database: " + documentsWithFiltrFromDatabase);
        Criteria projectCriteriaFromSql = new Criteria();
        Criterion projectCriterion = new Criterion("name", Operator.Equals, "newu");
        projectCriteriaFromSql.addCriterion(projectCriterion);
        projectCriteriaFromSql.setClassType(Document.class);
        List<Persistable> projectsFromDatabase = sqlPersistenceService.read(docCriteriaFromSql);
        System.out.println("Projects from database: " + projectsFromDatabase);

        Criteria docCriteriaFromFile = new Criteria();
        docCriteriaFromFile.setClassType(Document.class);
        docCriteriaFromFile.setFilePath("D:\\JSON\\test.json");
        docCriteriaFromFile.addCriterion("name", Operator.Equals, "Report1");
        List<Persistable> docsFromFile = persistenceService.read(docCriteriaFromFile);
        System.out.println("Documents from file: " + docsFromFile);

        Criteria projectCriteriaFromFile = new Criteria();
        projectCriteriaFromFile.setClassType(Project.class);
        projectCriteriaFromFile.setFilePath("D:\\JSON\\test.json");
        projectCriteriaFromFile.addCriterion("name", Operator.Equals, "Project1");
        List<Persistable> projectsFromFile = persistenceService.read(projectCriteriaFromFile);
        System.out.println("Projects from file: " + projectsFromFile);

        String jsonString = "{\"project\":[{\"id\":1,\"name\":\"Project1\"}, {\"id\":2,\"name\":\"Project2\"}],\"document\":[{\"id\":1,\"name\":\"doc1\",\"content\":\"This is the description of project 1.\"}, {\"id\":2,\"name\":\"Project2\",\"content\":\"This is the description of project 2.\"}]}";
        Criteria criteriaJsonForDoc = new Criteria();
        criteriaJsonForDoc.setClassType(Document.class);
        criteriaJsonForDoc.setJsonString(jsonString);
        criteriaJsonForDoc.addCriterion("name", Operator.Equals, "doc1");
        List<Persistable> documentsFromString = persistenceService.read(criteriaJsonForDoc);
        System.out.println("Documents from String: " + documentsFromString);

        Criteria criteriaJsonForProject = new Criteria();
        criteriaJsonForProject.setClassType(Project.class);
        criteriaJsonForProject.setJsonString(jsonString);
        criteriaJsonForProject.addCriterion("name", Operator.Equals, "Project1");
        List<Persistable> projectsFromString = persistenceService.read(criteriaJsonForProject);
        System.out.println("Projects from String: " + projectsFromString);
        sqlService.dropTable(Project.class);
        sqlService.dropTable(Document.class);
    }
}
