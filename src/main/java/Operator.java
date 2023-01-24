enum Operator {
    Equals("="),
    LessThan("<"),
    GreaterThan(">"),
    LessThanOrEqualTo("<="),
    GreaterThanOrEqualTo(">="),
    Contains("LIKE"),
    StartsWith("LIKE"),
    EndsWith("LIKE");

    private final String sqlOperator;

    Operator(String sqlOperator) {
        this.sqlOperator = sqlOperator;
    }

    public String getSqlOperator() {
        return sqlOperator;
    }

    public boolean evaluate(Object data, Object value) {
        if (this == Operator.Equals) {
            return data.equals(value);
        } else if (this == Operator.GreaterThan) {
            if (data instanceof Comparable) {
                return ((Comparable) data).compareTo(value) > 0;
            }
        } else if (this == LessThan) {
            if (data instanceof Comparable) {
                return ((Comparable) data).compareTo(value) < 0;
            }
        } else if (this == GreaterThanOrEqualTo) {
            if (data instanceof Comparable) {
                return ((Comparable) data).compareTo(value) >= 0;
            }
        } else if (this == LessThanOrEqualTo) {
            if (data instanceof Comparable) {
                return ((Comparable) data).compareTo(value) <= 0;
            }
        } else if (this == Contains) {
            return ((String) data).contains((String) value);
        } else if (this == StartsWith) {
            return ((String) data).startsWith((String) value);
        } else if (this == EndsWith) {
            return ((String) data).endsWith((String) value);
        }
        return false;
    }
}