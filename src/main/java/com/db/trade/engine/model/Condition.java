package com.db.trade.engine.model;

import java.util.HashMap;
import java.util.Map;

public class Condition {

    private String field;
    private Object value;
    private Operator operator;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public static enum Operator {
        NOT_EQUAL_TO("NOT_EQUAL_TO"),
        EQUAL_TO("EQUAL_TO"),
        GREATER_THAN("GREATER_THAN"),
        LESS_THAN("LESS_THAN"),
        GREATER_THAN_OR_EQUAL_TO("GREATER_THAN_OR_EQUAL_TO"),
        LESS_THAN_OR_EQUAL_TO("LESS_THAN_OR_EQUAL_TO");
        private final String value;
        private static Map<String, Operator> constants = new HashMap<String, Operator>();

        static {
            for (Operator c : values()) {
                constants.put(c.value, c);
            }
        }

        private Operator(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static Operator fromValue(String value) {
            Operator constant = constants.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }
    }

}
