package pro.andronov.jdml;

import org.postgresql.ds.PGSimpleDataSource;
import pro.andronov.jdml.lib.Column;
import pro.andronov.jdml.lib.DataSet;
import pro.andronov.jdml.lib.DataSetFactory;
import pro.andronov.jdml.lib.Row;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

public class test {

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setApplicationName("test");
        source.setServerName("localhost");
        source.setPortNumber(5433);
        source.setDatabaseName("vp");
        source.setUser("postgres");
        source.setPassword("1");

        DataSetFactory.get(source);

        DataSet paid = DataSetFactory.get().initDataSet("payments", "id", "SELECT nextval('payments_id_seq'::regclass)");

        System.out.println(paid.loadAll());

        /*Calendar cal = Calendar.getInstance();

        for(int i=0; i<10; i++) {
            cal.add(Calendar.DATE, i);
            Row r = paid.add();
            r.setValue("user_id", 79270224207l);
            r.setValue("pay_date", cal.getTime());
            r.setValue("amount", Math.random());
        }

        System.out.println(paid.save().size());*/
    }
}
