package com.andronovman.jdml.lib;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

public class EntityMetadata {
    private String table;
    private Map<String, Field> fields;
    private String pkColumn;
    public EntityMetadata() {
        fields = new LinkedHashMap<>();
    }

    public String getTable() {
        return table;
    }
    public void setTable(String name) {
        table = name;
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public void addField(String name, Field field) {
        fields.put(name, field);
        if(field.isPk)
            pkColumn = field.getName();
    }

    public String getPkColumn() {
        return pkColumn;
    }

    @Override
    public String toString() {
        return "EntityMetadata{" +
                "table='" + table + '\'' +
                ", fields=" + fields +
                ", pkColumn='" + pkColumn + '\'' +
                '}';
    }

    @Data
    class Field {
        private String name;
        private String sequence;
        private Class javaClass;
        private int sqlType = 0;
        private boolean isPk = false;
        private boolean isTransient = false;
    }
}
