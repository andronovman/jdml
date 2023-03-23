package pro.andronov.jdml.lib;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;


public class DataSet {
    private DataSource datasource;
    private String table, pk, sequence;
    private String lastQuery;
    private Object[] lastParameters;
    private long lastElapsedTime = 0;
    private List<Row> rows;
    private List<Column> cols;

    public DataSet() {
        rows = new ArrayList<Row>();
        cols = new ArrayList<Column>();
    }

    public DataSet(DataSource datasource) {
        this();
        this.datasource = datasource;
    }

    public String getTable() {
        return table;
    }

    public String getPK() {
        return pk;
    }

    public String getSequence() {
        return sequence;
    }

    public String getLastQuery() {
        return lastQuery;
    }

    public Object[] getLastParameters() {
        return lastParameters;
    }

    public long getLastElapsedTime() {
        return lastElapsedTime;
    }

    public void init(String table, String pk, String sequence) throws SQLException {
        this.table = table;
        this.pk = pk;
        this.sequence = sequence;

        String sql = String.format("SELECT * FROM %s WHERE 1=2", this.table);

        try (Connection con = datasource.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            cols = extractMetadata(rs);
        } catch (SQLException ex) {
            throw new SQLException(String.format("SQL: \"%s\"", sql), ex);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Column getColumn(String name) {
        for (Column c : getColumns())
            if(c.getName().equalsIgnoreCase(name))
                return c;
        return null;
    }

    public List<Column> getColumns() {
        return cols;
    }

    public List<Row> getRows() {
        return rows;
    }

    public List<Row> loadAll() throws SQLException {
        lastQuery = String.format("SELECT * FROM %s", table);
        lastParameters = null;
        lastElapsedTime = System.currentTimeMillis();
        try (Connection con = datasource.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(lastQuery)) {
            rows.clear();
            while (rs.next()) {
                Row row = new Row(this);
                for (Column c : cols) {
                    row.initValue(c.getName(), rs.getObject(c.getName(), c.getJavaClass()));
                }
                rows.add(row);
            }
            lastElapsedTime = (System.currentTimeMillis() - lastElapsedTime);
        } catch (SQLException ex) {
            throw new SQLException(String.format("SQL: \"%s\"; Params: []", lastQuery), ex);
        }
        return rows;
    }

    public List<Row> find(String query, Object... params) throws SQLException {
        lastQuery = String.format("SELECT * FROM %s %s", table, query);
        lastParameters = params;
        lastElapsedTime = System.currentTimeMillis();
        try (Connection con = datasource.getConnection();
             PreparedStatement stmt = con.prepareStatement(lastQuery)) {
            setParameters(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                rows.clear();
                while (rs.next()) {
                    Row row = new Row(this);
                    for (Column c : cols) {
                        row.initValue(c.getName(), rs.getObject(c.getName(), c.getJavaClass()));
                    }
                    rows.add(row);
                }
                lastElapsedTime = (System.currentTimeMillis() - lastElapsedTime);
            } catch (SQLException ex) {
                throw new SQLException(String.format("SQL: \"%s\"; Params: %s", lastQuery, Arrays.toString(lastParameters)), ex);
            }
        } catch (SQLException ex) {
            throw new SQLException(String.format("SQL: \"%s\"; Params: %s", lastQuery, Arrays.toString(lastParameters)), ex);
        }
        return rows;
    }

    public List<Row> save() {
        List<Row> completed = new ArrayList<>();
        return rows.stream().filter(row -> {
            return row.hasChanges();
        }).collect(Collectors.toList());
    }

    public Row add() {
        Row row = new Row(this);
        row.asNew(true);
        if(sequence != null) {
            row.setValue(pk, DataSetFactory.get(datasource).selectValue(sequence));
        }
        rows.add(row);
        return row;
    }

    private List<Column> extractMetadata(ResultSet rs) throws SQLException, ClassNotFoundException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        List<Column> result = new ArrayList<Column>(cols);
        for (int i = 1; i <= cols; i++) {
            Column r = new Column(this);
            r.setName(meta.getColumnName(i));
            r.setSqlType(meta.getColumnType(i));
            r.setSqlTypeName(meta.getColumnTypeName(i));
            r.setJavaClass(Converter.getJavaClass(meta.getColumnClassName(i)));
            r.setRequired((meta.isNullable(i) == 0));
            result.add(r);
        }
        return result;
    }

    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int index = 0; index < params.length;) {
            Object param = params[index++];
            if (param instanceof byte[]) {
                byte[] bytes = (byte[]) param;
                try {
                    Blob blob = ps.getConnection().createBlob();
                    if (blob == null) { // SQLite
                        ps.setBytes(index, bytes);
                    } else {
                        blob.setBytes(1, bytes);
                        ps.setBlob(index, blob);
                    }
                } catch (AbstractMethodError | SQLException e) {// net.sourceforge.jtds.jdbc.ConnectionJDBC2.createBlob is abstract :)
                    ps.setObject(index, param);
                }
            }
            if (param instanceof java.util.Date) {
                ps.setDate(index, new java.sql.Date(((java.util.Date) param).getTime()));
            } else {
                ps.setObject(index, param);
            }
        }
    }
}
