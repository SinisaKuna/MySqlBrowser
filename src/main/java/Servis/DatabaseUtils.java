package Servis;

import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;
import java.sql.*;

import static Servis.DatabaseConnection.getConnection;

public class DatabaseUtils {
    public static <T> boolean insertIntoTable(String tableName, T data) {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean success = false;

        try {
            conn = getConnection();

            // formiraj SQL INSERT upit na temelju naziva tablice i podataka iz objekta
            StringBuilder sb = new StringBuilder("INSERT INTO ");
            sb.append(tableName);
            sb.append("(");

            Field[] fields = new Field[0];
            Class clazz = data.getClass();
            while (clazz != null) {
                fields = ArrayUtils.addAll(fields, clazz.getDeclaredFields());
                clazz = clazz.getSuperclass();
            }
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                sb.append(fields[i].getName());
                if (i != fields.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append(") VALUES (");
            for (int i = 0; i < fields.length; i++) {
                sb.append("?");
                if (i != fields.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")");

            statement = conn.prepareStatement(sb.toString());

            // postavi vrijednosti parametara iz objekta u PreparedStatement
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                Object value = fields[i].get(data);
                statement.setObject(i + 1, value);
            }

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                success = true;
            }

        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }
    public static <T> boolean updateTable(String tableName, T data, String idColumnName, int id) {
        Connection conn = null;
        PreparedStatement statement = null;
        boolean success = false;

        try {
            conn = getConnection();

            // formiraj SQL UPDATE upit na temelju naziva tablice i naziva stupaca
            StringBuilder sb = new StringBuilder("UPDATE ");
            sb.append(tableName);
            sb.append(" SET ");

            Field[] fields = new Field[0];
            Class clazz = data.getClass();
            while (clazz != null) {
                fields = ArrayUtils.addAll(fields, clazz.getDeclaredFields());
                clazz = clazz.getSuperclass();
            }

            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                if (fields[i].getName() != idColumnName) {
                    sb.append(fields[i].getName());
                    sb.append("=?");
                    if (i != fields.length - 1) {
                        sb.append(", ");
                    }
                }

            }
            sb.append(" WHERE ");
            sb.append(idColumnName);
            sb.append("=?");

            statement = conn.prepareStatement(sb.toString());

            // postavi vrijednosti parametara iz objekta u PreparedStatement
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                if (fields[i].getName() != idColumnName) {
                    Object value = fields[i].get(data);
                    statement.setObject(i , value);
                }
            }

            // postavi id parametar
            statement.setObject(fields.length , id);


            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                success = true;
            }

        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }
    public static boolean deleteRow(String tableName, String idColumnName, int id) {
        Connection conn = null;
        PreparedStatement statement = null;
        boolean success = false;

        try {
            conn = getConnection();

            // formiraj SQL DELETE upit na temelju naziva tablice i naziva stupca
            String sql = "DELETE FROM " + tableName + " WHERE " + idColumnName + " = ?";

            statement = conn.prepareStatement(sql);

            // postavi id parametar
            statement.setObject(1, id);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                success = true;
            }

        } catch (SQLException ex) {
            String greska = ex.getMessage();
            JOptionPane.showMessageDialog(null,  greska,"Greška", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }
    public static ResultSet selectFromTable(String nazivTablice, String nazivPolja, int vrijednostPolja) {
        ResultSet rs = null;
        try {
            // priprema SQL upita s parametrima
            Connection conn = getConnection();
            String sql = "SELECT * FROM " + nazivTablice + " WHERE " + nazivPolja + " = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, vrijednostPolja);

            // izvršavanje upita i dobivanje resultseta
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }
//    public static void connectToDatabase (String tableName, JTable table) {
//        try {
//            TableModelFactory factory = new TableModelFactory();
//            DefaultTableModel model = factory.createTableModel(tableName);
//            table.setModel(model);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void connectToDatabase (String query, JTable table) {
        try {
            TableModelFactory factory = new TableModelFactory();
            DefaultTableModel model = factory.createTableModel(query);
            table.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
