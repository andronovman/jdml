package pro.andronov.jdml.lib;

import java.io.*;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Converter {
    public static Class getJavaClass(String className) throws ClassNotFoundException {
        Class cls = Class.forName(className);
        if (cls == Date.class || cls == java.sql.Date.class || cls == java.sql.Timestamp.class || cls == oracle.sql.TIMESTAMP.class) {
            return Date.class;
        } else if (cls == Clob.class || cls == oracle.sql.CLOB.class || cls == oracle.jdbc.OracleClob.class) {
            return String.class;
        } else if (cls == byte[].class || cls == Blob.class || cls == oracle.sql.BLOB.class || cls == oracle.jdbc.OracleBlob.class) {
            return byte[].class;
        } else {
            return cls;
        }
    }

    public static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    public static String toString(Object obj, String ifNull) throws IOException {
        String s = toString(obj);
        return s == null ? ifNull : s;
    }

    public static String toString(Object obj) throws IOException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Float || obj instanceof Double || obj instanceof BigDecimal) {
            return String.format("%.9f", obj);
        } else if (obj instanceof Date) {
            return String.valueOf(obj);
        } else if (obj instanceof Clob || obj instanceof oracle.sql.CLOB) {
            Reader r = null;
            StringWriter sw = null;
            Clob clob = (Clob) obj;
            try {
                clob.getAsciiStream();
                r = clob.getCharacterStream();
                sw = new StringWriter();

                char[] buffer = new char[4096];
                for (int n = 0; -1 != (n = r.read(buffer));) {
                    sw.write(buffer, 0, n);
                }
                return sw.toString();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                sw.close();
                r.close();
            }
        } else if(obj instanceof byte[]) {
            byte[] byteArray = (byte[]) obj;
            StringBuffer hexStringBuffer = new StringBuffer();
            for (int i = 0; i < byteArray.length; i++) {
                hexStringBuffer.append(byteToHex(byteArray[i]));
            }
            return hexStringBuffer.toString();
        }
        return String.valueOf(obj);
    }

    public static Number toNumber(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Number) {
            return (Number) obj;
        } else if (obj instanceof String) {
            try {
                return Double.valueOf(String.valueOf(obj).trim().replace(",", "."));
            } catch (NumberFormatException ex) {
                return null;
            }
        } else if (obj instanceof Date) {
            return ((Date) obj).getTime();
        } else if (obj instanceof Boolean) {
            return ((Boolean) obj) ? 1 : 0;
        }

        return null;
    }

    public static Short toShort(Object obj) {
        Number n = toNumber(obj);
        if (n != null) {
            return n.shortValue();
        }
        return null;
    }

    public static Integer toInteger(Object obj) {
        Number n = toNumber(obj);
        if (n != null) {
            return n.intValue();
        }
        return null;
    }

    public static Long toLong(Object obj) {
        Number n = toNumber(obj);
        if (n != null) {
            return n.longValue();
        }
        return null;
    }

    public static Float toFloat(Object obj) {
        Number n = toNumber(obj);
        if (n != null) {
            return n.floatValue();
        }
        return null;
    }

    public static Double toDouble(Object obj) {
        Number n = toNumber(obj);
        if (n != null) {
            return n.doubleValue();
        }
        return null;
    }

    public static BigDecimal toBigDecimal(Object obj) {
        Number n = toNumber(obj);
        if (n != null) {
            return new BigDecimal(n.doubleValue());
        }
        return null;
    }

    public static java.util.Date toDate(Object obj, String format) throws IOException, ParseException {
        DateFormat formatter = new SimpleDateFormat(format);
        if (obj instanceof java.sql.Timestamp) {
            return new java.util.Date(((java.sql.Timestamp) obj).getTime());
        } else if (obj instanceof oracle.sql.TIMESTAMP) {
            try {
                return ((oracle.sql.TIMESTAMP) obj).dateValue();
            } catch (SQLException ex) {
                return null;
            }
        } else if (obj instanceof java.util.Date) {
            return (java.util.Date) obj;
        } else if (obj instanceof Number) {
            return new java.util.Date(((Number) obj).longValue());
        } else if (obj instanceof String) {
            return formatter.parse(String.valueOf(obj));
        }
        return null;
    }

    public static java.sql.Date toSqlDate(Object obj, String format) throws IOException, ParseException {
        java.util.Date date = toDate(obj, format);
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }

    public static java.sql.Time toSqlTime(Object obj, String format) throws IOException, ParseException {
        java.util.Date date = toDate(obj, format);
        if (date == null) {
            return null;
        }
        return new java.sql.Time(date.getTime());
    }

    public static java.sql.Timestamp toSqlTimestamp(Object obj, String format) throws IOException, ParseException {
        java.util.Date date = toDate(obj, format);
        if (date == null) {
            return null;
        }
        return new java.sql.Timestamp(date.getTime());
    }

    public static byte[] toBytes(Object obj) throws IOException, SQLException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return String.valueOf(obj).getBytes();
        } else if (obj instanceof Boolean) {
            return new byte[]{(byte) (((boolean) obj) ? 1 : 0)};
        } else if (obj instanceof Byte) {
            return ByteBuffer.allocate(1).put((byte) obj).array();
        } else if (obj instanceof Short) {
            return ByteBuffer.allocate(2).putShort((short) obj).array();
        } else if (obj instanceof Integer) {
            return ByteBuffer.allocate(4).putInt((int) obj).array();
        } else if (obj instanceof Long) {
            return ByteBuffer.allocate(8).putLong((long) obj).array();
        } else if (obj instanceof Float) {
            return ByteBuffer.allocate(4).putFloat((float) obj).array();
        } else if (obj instanceof Double) {
            return ByteBuffer.allocate(8).putDouble((double) obj).array();
        } else if (obj instanceof File) {
            try (ByteArrayOutputStream os = new ByteArrayOutputStream(1024)) {
                FileInputStream is = new FileInputStream((File) obj);
                byte[] bytes = new byte[1024];
                int len;
                while ((len = is.read(bytes)) != -1) {
                    os.write(bytes, 0, len);
                }
                return os.toByteArray();
            }
        } else if (obj instanceof InputStream) {
            InputStream is = (InputStream) obj;
            try (ByteArrayOutputStream os = new ByteArrayOutputStream(1024)) {
                byte[] bytes = new byte[1024];
                int len;
                while ((len = is.read(bytes)) != -1) {
                    os.write(bytes, 0, len);
                }
                return os.toByteArray();
            }
        } else if (obj instanceof Blob) {
            Blob blob = (Blob) obj;
            try (InputStream is = blob.getBinaryStream()) {
                ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
                byte[] bytes = new byte[1024];
                int len;
                while ((len = is.read(bytes)) != -1) {
                    os.write(bytes, 0, len);
                }
                byte[] res = os.toByteArray();
                os.close();
                return res;
            }
        }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            return bos.toByteArray();

        }
    }

    public static Boolean toBoolean(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Boolean) {
            return (Boolean) obj;
        } else if (obj instanceof Byte) {
            return ((Byte) obj) == 1 || ((Byte) obj) == '1';
        } else if (obj instanceof BigDecimal) {
            return obj.equals(BigDecimal.ONE);
        } else if (obj instanceof Number) {
            return ((Number) obj).intValue() == 1;
        } else if (obj instanceof Character) {
            char c = (Character) obj;
            return c == 't' || c == 'T' || c == 'y' || c == 'Y' || c == '1';
        } else {
            String str = String.valueOf(obj);
            return str.equalsIgnoreCase("true") || str.equalsIgnoreCase("t")
                    || str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("y")
                    || str.equals("1") || Boolean.parseBoolean(str);
        }
    }

    public static boolean compareValue(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null && o2 == null) {
            return true;
        }
        if (o1 != null && o2 == null) {
            if (o1 instanceof String) {
                if (((String) o1).isEmpty()) {
                    return true;
                }
            }
            return false;
        }
        if (o1 == null && o2 != null) {
            if (o2 instanceof String) {
                if (((String) o2).isEmpty()) {
                    return true;
                }
            }
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1 instanceof Number && o2 instanceof Number) {
            if (((Number) o1).doubleValue() == ((Number) o2).doubleValue()) {
                return true;
            }
        }
        if (o1.toString().equalsIgnoreCase(o2.toString())) {
            return true;
        }
        return false;
    }
}
