package pro.andronov.jdml;

import org.postgresql.ds.PGSimpleDataSource;

import java.sql.SQLException;

public class test {

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setApplicationName("test");
        source.setServerName("localhost");
        source.setPortNumber(5433);
        source.setDatabaseName("vp");
        source.setUser("postgres");
        source.setPassword("1");

    }
}
