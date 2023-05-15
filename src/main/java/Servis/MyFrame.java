package Servis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

import static Servis.MySQLHelper.getForeignKeys;

public class MyFrame extends JFrame {
    public MyFrame(JFrame parent,String[] tables) throws SQLException {
        super("Moja aplikacija");

        // Kreiraj kontejner u koji cemo staviti gumbe
        Container contentPane = getContentPane();

        // Postavimo layout manager
        int numRows = tables.length + 1;
        contentPane.setLayout(new GridLayout(tables.length + 1, 1, 5, 5)); // dodali smo razmake od 5 piksela

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Kreiraj gumbe za svaki string u tables
        for (String table : tables) {
            String[] polja = getForeignKeys(table);
            JButton button = new JButton();
            if (polja.length != 0) {
                button.setText(table + " [id]");
            }
            else
            {
                button.setText(table);
            }

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    // Ovo će se izvršiti kada se gumb klikne
                    new frmBrowse(parent, table);
                    setVisible(true);
                }
            });
            contentPane.add(button);
        }

        // Kreiraj gumb za kraj rada
        JButton closeButton = new JButton("kraj rada");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                // Ovo će se izvršiti kada se gumb klikne
                System.exit(0);
            }
        });
        contentPane.add(closeButton);

        // Podesimo velicinu i vidljivost prozora
        int buttonHeight = 40;
        int windowHeight = numRows * buttonHeight + 50; // dodajemo 50 za margine
        setSize(300, windowHeight);
        setVisible(true);
    }
}
