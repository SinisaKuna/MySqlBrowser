package Servis;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class DatabaseConnectionFrame extends JFrame {
    public JTextField portField;
    public JTextField usernameField;
    public JPasswordField passwordField;
    private JButton connectButton;
    private JButton cancelButton;
    public JComboBox<String> databaseComboBox;
    private String[] databaseNames;
    public String connectionString;

    public DatabaseConnectionFrame(String port, String user, String pass) {
        setTitle("Spajanje na MySQL server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(250, 250);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 5, 5);

        // Port
        JLabel portLabel = new JLabel("Port:");
        portField = new JTextField(port,10);
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(portLabel, constraints);
        constraints.gridx = 1;
        panel.add(portField, constraints);

        // Korisničko ime
        JLabel usernameLabel = new JLabel("Korisničko ime:");
        usernameField = new JTextField(user,10);
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(usernameLabel, constraints);
        constraints.gridx = 1;
        panel.add(usernameField, constraints);

        // Lozinka
        JLabel passwordLabel = new JLabel("Lozinka:");
        passwordField = new JPasswordField(pass,10);
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(passwordLabel, constraints);
        constraints.gridx = 1;
        panel.add(passwordField, constraints);

        // Gumb za spajanje
        connectButton = new JButton("Spoji se");
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(connectButton, constraints);

        // Gumb za otkazivanje
        cancelButton = new JButton("Otkazi");
        constraints.gridx = 1;
        panel.add(cancelButton, constraints);

        // Combo box za odabir baza podataka
        databaseComboBox = new JComboBox<>();
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        panel.add(databaseComboBox, constraints);
        databaseComboBox.setEnabled(false);

        // Dodavanje panela u prozor
        add(panel);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String port = portField.getText().trim();
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (port.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(DatabaseConnectionFrame.this, "Molimo ispunite sva polja.", "Greška", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String url = "jdbc:mysql://localhost:" + port;

                try {
                    // Spajanje na MySQL server
                    Connection connection = DriverManager.getConnection(url, username, password.replace("*",""));

                    // Izvršavanje SQL upita za dobivanje liste svih baza podataka
                    DatabaseMetaData metaData = connection.getMetaData();
                    ResultSet resultSet = metaData.getCatalogs();

                    // Prikupljanje imena svih baza podataka u niz
                    int size = 0;
                    while (resultSet.next()) {
                        size++;
                    }
                    databaseNames = new String[size];
                    resultSet.beforeFirst();
                    int i = 0;
                    while (resultSet.next()) {
                        String databaseName = resultSet.getString("TABLE_CAT");
                        databaseNames[i++] = databaseName;
                    }

                    // Zatvaranje resursa
                    resultSet.close();
//                    connection.close();

                    // Omogućavanje combo boxa za odabir baza podataka
                    databaseComboBox.setModel(new DefaultComboBoxModel<>(databaseNames));
                    databaseComboBox.setEnabled(true);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(DatabaseConnectionFrame.this, "Greška prilikom spajanja na MySQL server.", "Greška", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    public String getConnectionString() {
        return connectionString;
    }

    public static void main(String[] args) {
        DatabaseConnectionFrame frame = new DatabaseConnectionFrame("3308","root", "@betaStudio2017");
        frame.databaseComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
                String selectedDatabase = (String) comboBox.getSelectedItem();
                if (selectedDatabase != null) {
                    String port = frame.portField.getText().trim();
                    String username = frame.usernameField.getText().trim();
                    String password = new String(frame.passwordField.getPassword()).trim();
                    String connectionString = "jdbc:mysql://localhost:" + port + "/" + selectedDatabase + "?user=" + username + "&password=" + password;
                    frame.connectionString = connectionString;
                    System.out.println("Connection string: " + connectionString);


                }
            }
        });
    }
}


