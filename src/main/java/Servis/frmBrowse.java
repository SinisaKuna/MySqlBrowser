package Servis;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import static Servis.DatabaseConnection.getConnection;
import static Servis.DatabaseUtils.connectToDatabase;
import static Servis.MySQLHelper.*;

public class frmBrowse extends JFrame {

    private Integer id;
    private int idColumnIndex = -1;

    public frmBrowse(JFrame parent, String table_name) {



        setTitle("Tablica [ " + table_name + " ]");
        setSize(600, 480);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(1, 2));
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(gridBagLayout);

        setLocationRelativeTo(parent);

        JButton dodajButton = new JButton("Dodaj");
        JButton promijeniButton = new JButton("Promijeni");
        JButton brišiButton = new JButton("Obriši");
        JButton prikažiButton = new JButton("Prikaži");
        JButton btnCancel = new JButton("Kraj");


        JPanel panel1 = new JPanel(new GridLayout(5,1));

        panel1.add(dodajButton);
        panel1.add(promijeniButton);
        panel1.add(brišiButton);
        panel1.add(prikažiButton);
        panel1.add(btnCancel);
        // Kreiranje GUI-a
        JTable table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);

        //panel2.add(scrollPane, BorderLayout.CENTER);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 3.0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(panel1, constraints);

        // Postavljanje drugog panela
        constraints.weightx = 1.0;
        constraints.gridx = 1;
        constraints.gridy = 0;
        add(new JScrollPane(table), constraints);

        // Spajanje na MySQL bazu podataka
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + table_name);
            // Popunjavanje tablice s podacima iz baze podataka
            table.setModel(buildTableModel(rs));

            // Zatvaranje veze s bazom podataka
            rs.close();
            stmt.close();
//            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }

        String identityField;
        try {
            identityField = getPrimaryKeyName(table_name);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        // Pronalazak indeksa stupca koji sadrži ID-ove
//        int idColumnIndex = -1;
        for (int i = 0; i < table.getColumnCount(); i++) {
            String columnName = table.getColumnName(i);
            if (columnName.equals(identityField)) {
                idColumnIndex = i;
                break;
            }
        }
        // Dodavanje ListSelectionListener-a na JTable
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        // Dohvaćanje ID selektiranog retka
                        Object idObject = table.getValueAt(selectedRow, idColumnIndex);
                        id = Integer.parseInt(idObject.toString());
                    }
                }
            }
        });

        dodajButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new boxBrowse(table_name, "0");
                try {
                    id = getMaxId(table_name, identityField);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                String query = null;
                try {
                    query = getQueryForJoinTables(table_name);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                connectToDatabase(query, table);
                SelectRow(table, id);
                setVisible(true);

                //id = null;
            }
        });
        promijeniButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (id != null) {
                    new boxBrowse(table_name, id.toString());
                    String query = null;
                    try {
                        query = getQueryForJoinTables(table_name);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    connectToDatabase(query, table);
                    SelectRow(table, id);
                    setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(parent,  "Morate selektirati redak u tablici","Greška", JOptionPane.ERROR_MESSAGE);
                    //dispose();
                }
            }
        });
        prikažiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = null;
                try {
                    query = getQueryForJoinTables(table_name);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
//                new QueryResultFrame(parent, query);
                new QueryResultFrame(parent, query);

                connectToDatabase(query, table);
                if (id != null) {
                    SelectRow(table, id);
                }
//                setVisible(true);

                //id = null;
            }
        });
        brišiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (id != null) {
                    int choice = JOptionPane.showConfirmDialog(parent, "Jeste sigurni?", "Brisanje zapisa", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        String identityField;
                        try {
                            identityField = getPrimaryKeyName(table_name);
                        } catch (SQLException ex) {
                            String greska = ex.getMessage();
                            JOptionPane.showMessageDialog(null,  greska,"Greška", JOptionPane.ERROR_MESSAGE);
                            throw new RuntimeException(ex);
                        }
                        DatabaseUtils.deleteRow(table_name, identityField, id);
                        String query = null;
                        try {
                            query = getQueryForJoinTables(table_name);
                        } catch (SQLException ex) {
                            String greska = ex.getMessage();
                            JOptionPane.showMessageDialog(null,  greska,"Greška", JOptionPane.ERROR_MESSAGE);
                            throw new RuntimeException(ex);
                        }
                        connectToDatabase(query, table);
                        //setVisible(true);
                        id = null;
                    }

                } else {
                    JOptionPane.showMessageDialog(parent,  "Morate selektirati redak u tablici","Greška", JOptionPane.ERROR_MESSAGE);
                    //dispose();
                }
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
//                System.exit(0);
            }
        });
        String query = null;
        try {
            query = getQueryForJoinTables(table_name);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        connectToDatabase(query, table);
        setVisible(true);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }



    // Pomoćna metoda za izgradnju tablice iz ResultSet objekta
    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // Popunjavanje naziva stupaca tablice
        int columnCount = metaData.getColumnCount();
        String[] columnNames = new String[columnCount];



        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = metaData.getColumnName(i);
        }


        // Popunjavanje redaka tablice s podacima iz ResultSet objekta
        Object[][] data = new Object[100][columnCount];
        int rowCount = 0;
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                data[rowCount][i - 1] = rs.getObject(i);
            }
            rowCount++;
        }

        // Izgradnja DefaultTableModel objekta s podacima tablice
        return new DefaultTableModel(data, columnNames);
    }


    private void SelectRow(JTable table, int id_value){
        for (int i = 0; i < table.getRowCount(); i++) {
            int idRow = (int) table.getValueAt(i, idColumnIndex); // dohvaćanje vrijednosti ID stupca
            if (idRow == id_value) {
                // pronašli smo redak s traženom ID vrijednošću, označavamo ga u tablici
                table.setRowSelectionInterval(i, i);
                id = id_value;
                break; // prekidamo petlju nakon što smo pronašli traženi redak
            }
        }
    }

}
