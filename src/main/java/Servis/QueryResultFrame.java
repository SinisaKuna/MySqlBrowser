package Servis;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import static Servis.DatabaseConnection.getConnection;
import static Servis.MySQLHelper.getQueryForJoinTables;

public class QueryResultFrame extends JFrame {

    private JTable table;

    public QueryResultFrame(JFrame parent, String query) {
        setTitle("Rezultat");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        //setSize(800, 480);
        setPreferredSize(new Dimension(800, 600)); // Postavljanje preferirane veličine JFrame-a na 800 x 600 piksela

//        // Centriranje JFrame-a na sredinu ekrana
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        int screenWidth = screenSize.width;
//        int screenHeight = screenSize.height;
//        int frameWidth = getWidth();
//        int frameHeight = getHeight();
//        int x = (screenWidth - frameWidth) / 2;
//        int y = (screenHeight - frameHeight) / 2;
//        setLocation(x, y);
//        setLocationRelativeTo(parent);

        int x = 100;
        int y = 100;
        setLocation(x, y);

        try {
            // Uspostavljanje konekcije s bazom podataka
            Connection connection = getConnection();

            // Izvršavanje upita
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Dobivanje metapodataka o rezultatima upita
            ResultSetMetaData metaData = resultSet.getMetaData();

            // Dobivanje broja stupaca u rezultatu
            int columnCount = metaData.getColumnCount();

            // Kreiranje dvodimenzionalnog objekta podataka za JTable
            Object[][] data = new Object[100][columnCount]; // Pretpostavljamo maksimalno 100 redaka rezultata
            int row = 0;

            // Prikupljanje podataka iz ResultSet-a
            while (resultSet.next()) {
                for (int col = 1; col <= columnCount; col++) {
                    data[row][col - 1] = resultSet.getObject(col);
                }
                row++;
            }

            // Kreiranje JTable sa prikupljenim podacima
            table = new JTable(data, getColumnNames(metaData));

            // Postavljanje JTable unutar JScrollPane-a
            JScrollPane scrollPane = new JScrollPane(table);

            // Postavljanje JScrollPane-a kao sadržaja JFrame-a
            setContentPane(scrollPane);

            // Prikazivanje JFrame-a
            pack();
            setVisible(true);

            // Zatvaranje resursa
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String[] getColumnNames(ResultSetMetaData metaData) throws SQLException {
        int columnCount = metaData.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int col = 1; col <= columnCount; col++) {
            columnNames[col - 1] = metaData.getColumnName(col);
        }
        return columnNames;
    }

    public static void main(String[] args) throws SQLException {
        // Primjer korištenja metode
//        String query = "SELECT * FROM postolar";
        String query = getQueryForJoinTables("postolar");
        new QueryResultFrame(null, query);
    }
}
