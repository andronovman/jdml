package pro.andronov.jdml.lib;

import java.util.LinkedHashMap;

public class Row extends LinkedHashMap<String, Object> {
    public Row() {
        asNew(false);
    }
    public Row(DataSet dataSet) {
        this();
        put("__DATASET__", dataSet);
    }

    public DataSet getDataSet() {
        return (DataSet) get("__DATASET__");
    }

    public void initValue(String column, Object value) {
        put(column, value);
        put("__HASCHANGES__", false);
    }

    public void setValue(String column, Object value) {
        put(column, value);
        put("__HASCHANGES__", true);
    }

    public Object getValue(String column) {
        return get(column);
    }

    public Object getValue(String column, Object ifNull) {
        Object v = getValue(column);
        if(v == null)
            return ifNull;
        return v;
    }

    public Object getValue(Column column) {
        return getValue(column.getName());
    }

    public <T> T getValue(String column, Class<T> type) {
        Object v = getValue(column);
        if(v == null)
            return null;
        return type.cast(v);
    }

    public boolean hasChanges() {
        if(isNew())
            return true;
        return (get("HASCHANGES") == null) ? false : (boolean)get("__HASCHANGES__");
    }

    public void asNew(boolean flag) {
        put("__ISNEW__", flag);
    }

    public boolean isNew() {
        return (boolean)get("__ISNEW__");
    }

    public String toJSON() {
        StringBuilder b = new StringBuilder();
        b.append("{\n");
        DataSet ds = getDataSet();
        if(ds != null) {
            for (Column c : ds.getColumns()) {
                b.append("  \"").append(c.getName()).append("\": \"").append(getValue(c.getName(),"")).append("\";\n");
            }
            b.append("}");
        }
        return b.toString();
    }
}
