import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Criteria {
    private List<Criterion> criteria;
    private Class classType;
    private String filePath;
    private String jsonString;

    public Criteria(Class classType) {
        this.classType = classType;
    }

    public void setClassType(Class classType) {
        this.classType = classType;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Criteria() {
        this.criteria = new ArrayList<Criterion>();
    }

    public Class getClassType() {
        return classType;
    }

    public void addCriterion(String columnName, Operator operator, Object value) {
        this.criteria.add(new Criterion(columnName, operator, value));
    }

    public void addCriterion(Criterion criterion) {
        this.criteria.add(criterion);
    }

    public List<Criterion> getCriteria() {
        return criteria;
    }

    private String getJsonString(Object source) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        if (source instanceof File) {
            return mapper.writeValueAsString(mapper.readTree((File) source));
        } else if (source instanceof String) {
            return (String) source;
        } else {
            throw new IllegalArgumentException("Invalid source type. Must be either File or String.");
        }
    }


}
