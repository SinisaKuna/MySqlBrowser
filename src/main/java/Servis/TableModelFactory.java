package Servis;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

import static Servis.DatabaseConnection.getConnection;

public class TableModelFactory {
    public DefaultTableModel createTableModel(String query) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        DefaultTableModel model;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Create column names array
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i-1] = metaData.getColumnName(i);
            }

            // Create data array
            Object[][] data = new Object[0][columnCount];
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i-1] = resultSet.getObject(i);
                }
                data = addRowToData(data, row);
            }

            // Create table model
            model = new DefaultTableModel(data, columnNames);

            // Set column identifiers
            model.setColumnIdentifiers(columnNames);
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }

        return model;
    }

    // Helper method to add a row to a 2D array
    private Object[][] addRowToData(Object[][] data, Object[] row) {
        Object[][] newData = new Object[data.length + 1][row.length];
        for (int i = 0; i < data.length; i++) {
            newData[i] = data[i];
        }
        newData[data.length] = row;
        return newData;
    }
}
