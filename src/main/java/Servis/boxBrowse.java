package Servis;
import java.sql.*;
import javax.swing.*;
import java.awt.*;

import static Servis.DatabaseConnection.getConnection;
import static Servis.MySQLHelper.*;

public class boxBrowse extends JDialog {

    private JTextField[] textFields;

    public boxBrowse(String tableName,  String identityValue) {
        setTitle(tableName);

        try {

            String identityField = getPrimaryKeyName(tableName);
            Connection conn = getConnection();

            // Get column names for the specified table
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i-1] = metadata.getColumnName(i);
            }

            // Create labels and text fields for each column
            JPanel panel = new JPanel(new GridLayout(columnCount+2, 2, 10, 10));

            JLabel spacer1 = new JLabel("");
            JLabel spacer2 = new JLabel("");
            JLabel spacer3 = new JLabel("");
            JLabel spacer4 = new JLabel("");
            Dimension spacerSize = new Dimension(0, 10); // Promijenite visinu praznog labela po želji
            spacer1.setPreferredSize(spacerSize);
            spacer2.setPreferredSize(spacerSize);
            spacer3.setPreferredSize(spacerSize);
            spacer4.setPreferredSize(spacerSize);
            panel.add(spacer1);
            panel.add(spacer2);

            String[] polja = getForeignKeys(tableName);

            JLabel[] labels = new JLabel[columnCount];
            textFields = new JTextField[columnCount];
            for (int i = 0; i < columnCount; i++) {
                labels[i] = new JLabel(columnNames[i]);
                String ime = columnNames[i];
                if (provjeriImeUPolju(ime, polja))
                {
                    labels[i].setForeground(Color.RED);
                }

                panel.add(labels[i]);
                textFields[i] = new JTextField();

                Dimension labelSize = labels[i].getPreferredSize();
                Dimension textFieldSize = new Dimension(labelSize.width * 2, labelSize.height);
                textFields[i].setPreferredSize(textFieldSize);

                panel.add(textFields[i]);
                // Disable the text field if it corresponds to the identity field
                if (identityField != null && columnNames[i].equals(identityField)) {
                    textFields[i].setEnabled(false);
                }
            }

            add(panel, BorderLayout.CENTER);

            if (identityValue != "0") {
                // Fill in text fields with the specified identity value
                if (identityField != null && identityValue != null) {
                    conn = getConnection();
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE " + identityField + " = ?");
                    ps.setString(1, identityValue);
                    ResultSet rs2 = ps.executeQuery();
                    if (rs2.next()) {
                        for (int i = 0; i < columnCount; i++) {
                            String columnName = columnNames[i];
                            String value = rs2.getString(columnName);
                            textFields[i].setText(value);
                        }
                    }
                }
            }


            panel.add(spacer3);
            panel.add(spacer4);


            // Add buttons to save or cancel the dialog
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Spremi");
            saveButton.addActionListener(e -> {
                try {
                    SpremiMe(tableName, identityField, identityValue,textFields);
                } catch (SQLException ex) {
                    String greska = ex.getMessage();
                    JOptionPane.showMessageDialog(null,  greska,"Greška", JOptionPane.ERROR_MESSAGE);
                    throw new RuntimeException(ex);
                }
                dispose();
            });

            JButton cancelButton = new JButton("Odustani");
            cancelButton.addActionListener(e -> dispose());
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            add(buttonPanel, BorderLayout.SOUTH);

            pack();
            setLocationRelativeTo(null);
            setModal(true);
            setVisible(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void SpremiMe(String tableName, String identityField, String identityValue, JTextField[] textFields) throws SQLException {
        if (identityValue == "0")
        {
            insertTableRow(tableName,identityField, textFields);
        }
        else
        {
            updateTableRow(tableName, textFields, identityField,  identityValue);
        }
    }

    public void insertTableRow(String nazivTablice, String identityField, JTextField[] textFields) throws SQLException {

        String[] polja = getForeignKeys(nazivTablice);
        // Stvori spoj na bazu podataka
        Connection conn = getConnection();

        // Get column names for the specified table
        Statement selectstmt = conn.createStatement();
        ResultSet rs = selectstmt.executeQuery("SELECT * FROM " + nazivTablice);
        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i-1] = metadata.getColumnName(i);
        }

        // Stvori SQL upit za umetanje novog redka u tablicu
        String sql = "INSERT INTO " + nazivTablice + " (";
        for (int i = 0; i < columnCount; i++) {
            if (!columnNames[i].equals(identityField)) {


                if (provjeriImeUPolju(columnNames[i], polja))
                {
                    if (!textFields[i].getText().equals("")) {
                    sql += columnNames[i];
                    if (i < columnCount - 1) {
                        sql += ", ";
                    }
                }

                } else {
                    sql += columnNames[i];
                    if (i < columnCount - 1) {
                        sql += ", ";
                    }
                }
            }
        }
        sql += ") VALUES (";
        for (int i = 0; i < columnCount; i++) {
            if (!columnNames[i].equals(identityField)) {

                if (provjeriImeUPolju(columnNames[i], polja))
                {
                    if (!textFields[i].getText().equals("")) {
                        sql += "'" + textFields[i].getText() + "'";
                        if (i < columnCount - 1) {
                            sql += ", ";
                        }
                    }

                } else {
                    sql += "'" + textFields[i].getText() + "'";
                    if (i < columnCount - 1) {
                        sql += ", ";
                    }
                }
            }
        }
        sql += ")";
        sql = sql.replace(", )"," )");

        // Izvrši SQL upit
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);

        // Zatvori sve otvorene resurse
        stmt.close();
        conn.close();
    }


    public void updateTableRow(String nazivTablice, JTextField[] textFields, String identityField, String identityValue) throws SQLException {

        String[] polja = getForeignKeys(nazivTablice);
        // Stvori spoj na bazu podataka
        Connection conn = getConnection();

        // Get column names for the specified table
        Statement selectstmt = conn.createStatement();
        ResultSet rs = selectstmt.executeQuery("SELECT * FROM " + nazivTablice);
        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i-1] = metadata.getColumnName(i);
        }

        // Stvori SQL upit za ažuriranje redaka u tablici
        String sql = "UPDATE " + nazivTablice + " SET ";
        for (int i = 0; i < columnCount; i++) {
            if (!columnNames[i].equals(identityField)) {
                if (provjeriImeUPolju(columnNames[i], polja))
                {
                    if (!textFields[i].getText().equals("")) {
                        sql += columnNames[i] + "='" + textFields[i].getText() + "'";
                        if (i < columnCount - 1) {
                            sql += ", ";
                        }
                    } else {
                        sql += columnNames[i] + "= null";
                        if (i < columnCount - 1) {
                            sql += ", ";
                        }
                    }
                } else {
                    sql += columnNames[i] + "='" + textFields[i].getText() + "'";
                    if (i < columnCount - 1) {
                        sql += ", ";
                    }
                }
            }
        }
        sql += " WHERE " + identityField + "='" + identityValue + "'";
        sql = sql.replace(",  WHERE"," WHERE");
        // Izvrši SQL upit
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);

        // Zatvori sve otvorene resurse
        stmt.close();
        conn.close();
    }

}
