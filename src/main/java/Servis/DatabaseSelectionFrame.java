package Servis;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DatabaseSelectionFrame extends JFrame {
    private JComboBox<String> databaseComboBox;
    private JButton selectButton;
    private String selectedDatabase;

    public DatabaseSelectionFrame(String[] databaseNames) {
        setTitle("Odabir baze podataka");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);

        databaseComboBox = new JComboBox<>(databaseNames);
        selectButton = new JButton("Odaberi");

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedDatabase = (String) databaseComboBox.getSelectedItem();
                dispose();  // Zatvaranje prozora nakon odabira
            }
        });

        JPanel panel = new JPanel();
        panel.add(databaseComboBox);
        panel.add(selectButton);
        add(panel);

        setVisible(true);
    }

    public String getSelectedDatabase() {
        return selectedDatabase;
    }

    public static void main(String[] args) {
        String[] databaseNames = {"database1", "database2", "database3"};  // Primjer popisa baza podataka

        DatabaseSelectionFrame frame = new DatabaseSelectionFrame(databaseNames);

        // Ovaj dio koda čeka dok korisnik ne odabere bazu podataka
        while (frame.getSelectedDatabase() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String selectedDatabase = frame.getSelectedDatabase();
        System.out.println("Odabrana baza podataka: " + selectedDatabase);
    }
}
