package pro.andronov.jdml.lib;

import java.util.LinkedHashMap;

public class Column extends LinkedHashMap<String, Object> {

    public Column(DataSet dataSet) {
        put("DATASET", dataSet);
        setJavaClass(String.class);
        setDefaultValue(null);
    }

    public void setName(String name) {
        put("NAME", name);
    }

    public String getName() {
        return (String)get("NAME");
    }

    public DataSet getDataSet() {
        return (DataSet)get("DATASET");
    }

    public void setJavaClass(Class c) {
        put("JAVA_CLASS", c);
    }

    public Class getJavaClass() {
        return (Class)get("JAVA_CLASS");
    }

    public void setSqlType(int type) {
        put("SQL_TYPE", type);
    }

    public int getSqlType() {
        if(get("SQL_TYPE") == null)
            return 0;
        return (int)get("SQL_TYPE");
    }

    public void setSqlTypeName(String type) {
        put("SQL_TYPE_NAME", type);
    }

    public String getSqlTypeName() {
        return (String)get("SQL_TYPE_NAME");
    }

    public void setRequired(boolean flag) {
        put("REQUIRED", flag);
    }

    public boolean isRequired() {
        if(get("REQUIRED") == null)
            return false;
        return (Boolean)get("REQUIRED");
    }

    public void setDefaultValue(Object value) {
        put("DEFAULT_VALUE", value);
    }

    public Object getDefaultValue() {
        return get("DEFAULT_VALUE");
    }

    public <T> T getDefaultValue(Class<T> type) {
        Object val = getDefaultValue();
        if(val == null)
            return null;
        return type.cast(val);
    }

    @Override
    public String toString() {
        return getName();
    }
}
