import Servis.MyFrame;

import javax.swing.*;


import static Servis.MySQLHelper.getTableNames;

public class Glavna {

    public static void main(String args[]) throws Exception {
        // Calling the getTableNames method
        String[] tables = getTableNames();
        new MyFrame(null,tables);
    }
}

