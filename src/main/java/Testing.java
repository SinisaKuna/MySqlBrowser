import Servis.DatabaseSelectionFrame;

import static Servis.MySQLHelper.*;

public class Testing {
    public static void main(String[] args) {

        String[] databases = getDatabaseNames("3308","root","@beta*Studio2017".replace("*",""));
        System.out.println("Popis svih baza podataka:");
        for (String database : databases) {
            System.out.println(database);
        }

        DatabaseSelectionFrame frame = new DatabaseSelectionFrame(databases);

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
