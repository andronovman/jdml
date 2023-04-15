package com.andronovman.jdml.lib;

import com.andronovman.jdml.lib.annotations.Column;
import com.andronovman.jdml.lib.annotations.Entity;
import com.andronovman.jdml.lib.annotations.Id;
import com.andronovman.jdml.lib.annotations.Transient;
import com.andronovman.jdml.lib.enums.DataVendor;
import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

@Getter
@Setter
public class DataSet<T> {
    private String name;
    private Class<T> entityClass;
    private EntityMetadata entityMetadata;
    private List<T> rows;
    private DataVendor vendor;

    public DataSet() {
        rows = new ArrayList<>();
        vendor = null;
    }

    public DataSet(Class<T> entityClass) {
        this();
        this.entityClass = entityClass;
        try {
            extractMeta();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void extractMeta() throws InstantiationException, IllegalAccessException {
        entityMetadata = new EntityMetadata();
        entityMetadata.setTable(entityClass.getName().substring(entityClass.getName().lastIndexOf('.') + 1));
        for (Annotation a : entityClass.getDeclaredAnnotations()) {
            if (a instanceof Entity) {
                String driverName = Jdml.getInstance().getDataSource().getClass().getName().toLowerCase();
                if(driverName.contains("oracle")) {
                    vendor = DataVendor.ORACLE;
                } else if (driverName.contains("postgresql")) {
                    vendor = DataVendor.POSTGRES;
                } else if (driverName.contains("sqlserver")) {
                    vendor = DataVendor.MSSQL;
                } else if (driverName.contains("mysql")) {
                    vendor = DataVendor.MYSQL;
                } else if (driverName.contains("firebird")) {
                    vendor = DataVendor.FIREBIRD;
                }

                var entity = (Entity) a;
                if(entity.table() != null && !entity.table().isEmpty()) {
                    entityMetadata.setTable(((Entity) a).table());
                    break;
                }
            }
        }
        for (Field field : entityClass.getDeclaredFields()) {
            EntityMetadata.Field attr = entityMetadata.getFields().getOrDefault(field.getName(), entityMetadata.new Field());
            attr.setName(field.getName());
            attr.setJavaClass(field.getType());
            for (Annotation a : field.getDeclaredAnnotations()) {
                if (a instanceof Id) {
                    var id = (Id) a;
                    attr.setPk(true);
                    attr.setSequence(id.sequence());
                } else if (a instanceof Column) {
                    var column = (Column) a;
                    if (column.name() != null && !column.name().isEmpty())
                        attr.setName(column.name());
                    attr.setSqlType(column.sqlType());
                } else if (a instanceof Transient) {
                    attr.setTransient(true);
                }
            }
            entityMetadata.addField(field.getName(), attr);
        }
    }

    public List<T> loadALl() throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        DataSource dataSource = Jdml.getInstance().getDataSource();
        if (dataSource == null) {
            throw new NullPointerException("Current DataSource is null");
        }

        String sql = "SELECT * FROM "+entityMetadata.getTable();
        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            rows.clear();
            if (entityMetadata != null) {
                int i = 0;
                while (rs.next()) {
                    var row = entityClass.newInstance();
                    for (Map.Entry<String, EntityMetadata.Field> f : entityMetadata.getFields().entrySet()) {
                        if(f.getValue().isTransient())
                            continue;
                        Object value = rs.getObject(f.getValue().getName(), f.getValue().getJavaClass());
                        Field field = entityClass.getDeclaredField(f.getKey());
                        field.setAccessible(true);
                        field.set(row, value);
                    }
                    rows.add(row);
                }
            } else {
                //as Map
            }
            return rows;
        }
    }

    public T getById(Object id) throws NoSuchFieldException, IllegalAccessException {
        if(entityMetadata == null || entityMetadata.getPkColumn() == null) {
            throw new RuntimeException("PK column not defined");
        }
        for (T o : rows) {
            Field f = entityClass.getDeclaredField(entityMetadata.getPkColumn());
            f.setAccessible(true);
            if(f.get(o).equals(id)) {
                return o;
            }
        }
        return null;
    }
}
