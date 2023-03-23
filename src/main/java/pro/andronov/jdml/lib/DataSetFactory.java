package pro.andronov.jdml.lib;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DataSetFactory {
    private static Map<DataSource, DataSetFactory> instance;
    /*-----------------------------------*/
    private static DataSource currentDatasource = null;
    private DataSource dataSource = null;

    private DataSetFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static synchronized DataSetFactory get() {
        if (instance == null) {
            throw new NullPointerException("DataSetFactory instance is null. Call get() with DataSource parameter.");
        }
        return instance.get(currentDatasource);
    }

    public static synchronized DataSetFactory get(DataSource datasource) {
        if (instance == null) {
            instance = new HashMap<>();
            instance.put(datasource, new DataSetFactory(datasource));
        }
        currentDatasource = datasource;
        return instance.get(currentDatasource);
    }

    public DataSet emptyDataSet() {
        DataSet ds = new DataSet();
        return ds;
    }

    public DataSet initDataSet(String table, String pk, String sequence) throws SQLException {
        DataSet ds = new DataSet(currentDatasource);
        ds.init(table, pk, sequence);
        return ds;
    }

    public DataSet filledDataSet(String sql, Object...parameters) {
        DataSet ds = new DataSet(currentDatasource);
        return ds;
    }

    public Object selectValue(String sql, Object...params) {
        if(dataSource == null) {
            throw new NullPointerException("current DataSource is null");
        }
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            for (int index = 0; index < params.length;) {
                Object param = params[index++];
                if (param instanceof java.util.Date) {
                    stmt.setDate(index, new java.sql.Date(((java.util.Date) param).getTime()));
                } else {
                    stmt.setObject(index, param);
                }
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    return rs.getObject(1);
                }
                return null;
            } catch (SQLException ex) {
                throw new RuntimeException(String.format("SQL: \"%s\"; Params: %s", sql, Arrays.toString(params)), ex);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(String.format("SQL: \"%s\"; Params: %s", sql, Arrays.toString(params)), ex);
        }
    }
}
