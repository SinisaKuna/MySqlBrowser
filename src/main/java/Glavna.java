import Servis.DatabaseConnectionFrame;
import Servis.MyFrame;

import javax.swing.*;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static Servis.DatabaseConnection.setConnection;
import static Servis.FileUtils.*;
import static Servis.MySQLHelper.getTableNames;

public class Glavna {


//    public static void main(String args[]) throws Exception {
//        // Calling the getTableNames method
//        String[] tables = getTableNames();
//        new MyFrame(null,tables);
//    }


    public static void main(String[] args) throws IOException {

        String[] connectionInfoTemp = readTxt("dat/mysql_connection.txt", 4);

        String URL = connectionInfoTemp[0];
        String USERNAME = connectionInfoTemp[1].replace("*", "");
        String PASSWORD = connectionInfoTemp[2];
        String PORT  = connectionInfoTemp[3];

        DatabaseConnectionFrame frame = new DatabaseConnectionFrame(PORT,USERNAME,PASSWORD);
        frame.databaseComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
                String selectedDatabase = (String) comboBox.getSelectedItem();
                if (selectedDatabase != null) {
                    String port = frame.portField.getText().trim();
                    String username = frame.usernameField.getText().trim().replace("*","");
                    String password = new String(frame.passwordField.getPassword()).trim().replace("*","");
                    String connectionString = "jdbc:mysql://localhost:" + port + "/" + selectedDatabase + "?user=" + username + "&password=" + password;

                    String URL ="jdbc:mysql://localhost:" + port + "/" + selectedDatabase;
                    String USERNAME = username;
                    String PASSWORD = password;

                    String[] podacKonekcije = new String[4];
                    podacKonekcije[0] = URL;
                    podacKonekcije[1] = USERNAME;
                    podacKonekcije[2] = "*"+PASSWORD;
                    podacKonekcije[3] = port;

                    deleteTxt("dat/mysql_connection.txt");
                    writeToTxt(podacKonekcije, "dat/mysql_connection.txt");
                    frame.dispose();


//                    frame.connectionString = connectionString;
                    System.out.println("Connection string: " + connectionString);
                    try {
                        Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                        setConnection(conn);
                        String[] tables = getTableNames();
                        new MyFrame(null,tables);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }


                }
            }
        });
    }
}

