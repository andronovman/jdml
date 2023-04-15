package com.andronovman.jdml;

import com.andronovman.jdml.entities.Role;
import com.andronovman.jdml.entities.Test;
import com.andronovman.jdml.entities.User;
import com.andronovman.jdml.lib.DataSet;
import org.postgresql.ds.PGSimpleDataSource;
import com.andronovman.jdml.lib.Jdml;

import java.sql.SQLException;

public class test {

    public static void main(String[] args) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setApplicationName("test");
        source.setServerName("localhost");
        source.setPortNumber(5433);
        source.setDatabaseName("shortee");
        source.setUser("postgres");
        source.setPassword("1");

        Jdml.getInstance().addDataSource("local", source);
        Jdml.getInstance().switchDataSource("local");

        DataSet<Test> ds = Jdml.getInstance().createDataSet(Test.class);

        long t = System.currentTimeMillis();
        ds.loadALl();
        System.out.println("loadAll(): " + String.valueOf(System.currentTimeMillis() - t) + " ms.");

        t = System.currentTimeMillis();
        Test test = ds.getById(1500);
        System.out.println("getById(): " + String.valueOf(System.currentTimeMillis() - t) + " ms.");
        System.out.println(test);

    }
}
