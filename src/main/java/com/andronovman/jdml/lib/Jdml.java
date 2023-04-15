package com.andronovman.jdml.lib;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class Jdml {

    private static Jdml instance;
    private Map<String, DataSource> dataSources;
    private String currentDataSourceName;

    private Jdml() {
        dataSources = new HashMap<>();
    }
    public static synchronized Jdml getInstance() {
        if (instance == null) {
            instance = new Jdml();
        }
        return instance;
    }
    public void addDataSource(String name, DataSource dataSource) {
        currentDataSourceName = name.toUpperCase();
        dataSources.put(currentDataSourceName, dataSource);
    }

    public void switchDataSource(String name) {
        if(dataSources.keySet().contains(name.toUpperCase())) {
            currentDataSourceName = name.toUpperCase();
            return;
        }
        throw new RuntimeException("Data source named \""+name+"\" not found");
    }

    public DataSource getDataSource() {
        return dataSources.get(currentDataSourceName);
    }

    public <T> DataSet<T> createDataSet(Class<T> entityClass) {
        return new DataSet<T>(entityClass);
    }

}
