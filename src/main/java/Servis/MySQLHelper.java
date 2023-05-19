package Servis;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import static Servis.DatabaseConnection.getConnection;

public class MySQLHelper {

    // Metoda koja vraća polje stringova sa imenima svih kolona zadane tablice
    public static String[] getTableNames() throws Exception {
        //Getting the connection
        //String mysqlUrl = "jdbc:mysql://localhost/mydatabase";
        Connection con = getConnection();

        //Creating a Statement object
        Statement stmt = con.createStatement();

        //Retrieving the data
        ResultSet rs = stmt.executeQuery("Show tables");

        // Creating an ArrayList to store the table names
        ArrayList<String> tableNames = new ArrayList<>();

        // Adding the table names to the ArrayList
        while(rs.next()) {
            tableNames.add(rs.getString(1));
        }

        // Converting the ArrayList to an array of Strings
        String[] tablesArray = tableNames.toArray(new String[tableNames.size()]);

        // Returning the array of table names
        return tablesArray;
    }


    // Metoda koja vraća ime primarnog ključa za zadani naziv tablice
    public static String getPrimaryKeyName(String naziv_tablice) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String primaryKey = null;

        try {
            // Uspostavljamo vezu s bazom podataka
            con = getConnection();
            String dbName = con.getCatalog();
            // Pripremamo upit kojim ćemo dohvatiti informacije o primarnom ključu
            String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE CONSTRAINT_SCHEMA = ? AND TABLE_NAME = ? AND CONSTRAINT_NAME = 'PRIMARY'";
            pst = con.prepareStatement(sql);
            pst.setString(1, dbName);
            pst.setString(2, naziv_tablice);
            rs = pst.executeQuery();

            // Ako upit vrati rezultat, čitamo ime primarnog ključa iz ResultSet-a
            if (rs.next()) {
                primaryKey = rs.getString("COLUMN_NAME");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // Zatvaramo sve objekte koji su korišteni za dohvaćanje informacija o primarnom ključu
            if (rs != null) {
                rs.close();
            }
            if (pst != null) {
                pst.close();
            }
//            if (con != null) {
//                con.close();
//            }
        }

        // Vraćamo ime primarnog ključa ili null ako nije pronađen
        return primaryKey;
    }

    // Metoda koja vraća polje stringova za sve strane ključeve zadane tablice
    public static String[] getForeignKeys(String naziv_tablice) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String[] foreignKeys = null;
        int count = 0;

        try {
            // Uspostavljamo vezu s bazom podataka
            con = getConnection();
            String dbName = con.getCatalog();
            // Pripremamo upit kojim ćemo dohvatiti informacije o stranim ključevima
            String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE  CONSTRAINT_SCHEMA = ? AND TABLE_NAME = ? AND CONSTRAINT_NAME <> 'PRIMARY'";
//            pst = con.prepareStatement(sql);
            pst = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            pst.setString(1, dbName);
            pst.setString(2, naziv_tablice);
            rs = pst.executeQuery();

            // Brojimo koliko ima stranih ključeva u tablici
            while (rs.next()) {
                count++;
            }

            // Vraćamo ResultSet natrag na početak
            rs.beforeFirst();

            // Inicijaliziramo polje stringova za strane ključeve
            foreignKeys = new String[count];

            // Čitamo ime svakog stranog ključa i pohranjujemo ga u polje stringova
            int i = 0;
            while (rs.next()) {
                foreignKeys[i] = rs.getString("COLUMN_NAME");
                i++;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // Zatvaramo sve objekte koji su korišteni za dohvaćanje informacija o stranim ključevima
            if (rs != null) {
                rs.close();
            }
            if (pst != null) {
                pst.close();
            }
//            if (con != null) {
//                con.close();
//            }
        }

        // Vraćamo polje stringova za strane ključeve ili null ako nema stranih ključeva
        return foreignKeys;
    }


    public static int getMaxId(String tablica, String id_polje) throws SQLException {
        Connection con = null;
        int maxId = 0;
        try {
            con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX("+ id_polje+ ") FROM " + tablica);
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxId;
    }

    public static boolean provjeriImeUPolju(String ime, String[] poljeImena) {
            for (String imeUPolju : poljeImena) {
                if (imeUPolju.equals(ime)) {
                    return true;
                }
            }
        return false;
    }

    public static boolean provjeriImeUPolju(String ime, String[][] poljeImena) {
//        for (String imeUPolju : poljeImena) {
//            if (imeUPolju.equals(ime)) {
//                return true;
//            }
//        }
        return false;
    }

    public static String[][] getJoinData(String naziv_tablice) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String[][] foreignKeys = null;
        int count = 0;

        try {
            // Uspostavljamo vezu s bazom podataka
            con = getConnection();
            String dbName = con.getCatalog();
            // Pripremamo upit kojim ćemo dohvatiti informacije o stranim ključevima
            String sql = "SELECT * FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE  CONSTRAINT_SCHEMA = ? AND TABLE_NAME = ? AND CONSTRAINT_NAME <> 'PRIMARY'";
            pst = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            pst.setString(1, dbName);
            pst.setString(2, naziv_tablice);
            rs = pst.executeQuery();

            // Brojimo koliko ima stranih ključeva u tablici
            while (rs.next()) {
                count++;
            }

            // Vraćamo ResultSet natrag na početak
            rs.beforeFirst();

            // Inicijaliziramo polje stringova za strane ključeve
            foreignKeys = new String[count][4];

            // Čitamo ime svakog stranog ključa i pohranjujemo ga u polje stringova
            int i = 0;
            while (rs.next()) {
                foreignKeys[i][0] = rs.getString("TABLE_NAME");
                foreignKeys[i][1] = rs.getString("COLUMN_NAME");
                foreignKeys[i][2] = rs.getString("REFERENCED_TABLE_NAME");
                foreignKeys[i][3] = rs.getString("REFERENCED_COLUMN_NAME");
                i++;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // Zatvaramo sve objekte koji su korišteni za dohvaćanje informacija o stranim ključevima
            if (rs != null) {
                rs.close();
            }
            if (pst != null) {
                pst.close();
            }
//            if (con != null) {
//                con.close();
//            }
        }

        // Vraćamo polje stringova za strane ključeve ili null ako nema stranih ključeva
        return foreignKeys;
    }


    public static String getJoinQuery(String tablica, String[][] join){

        String query = "SELECT * FROM " + tablica + " " ;




        for (int i = 0; i < join.length; i++) {
            String naziv_tablice  = join[i][0];
            String naziv_kolone  = join[i][1];
            String referentna_tablica  = join[i][2];
            String referentna_kolona  = join[i][3];
            if (naziv_tablice.equals(referentna_tablica)) {
                query += " LEFT JOIN " + referentna_tablica + " AS " + referentna_kolona + " ON " + referentna_kolona + "." + referentna_kolona + " = " + naziv_tablice + "." + naziv_kolone;
            } else {
                query += " LEFT JOIN " + referentna_tablica + " AS " + referentna_tablica + " ON " + referentna_tablica + "." + referentna_kolona + " = " + naziv_tablice + "." + naziv_kolone;
            }
       }
        return query;
    }

    public static String getFinalJoinQuery(String tablica, String[][] kolone,  String[][] join) throws SQLException {


        String[][] kolone_tablice = getQueryFields("SELECT * FROM " + tablica);

        String query = "SELECT " ;
        for (String[] kolona : kolone) {
            if (kolona[0].isEmpty()){
                query +=  kolona[1] + ", ";
            } else {
                query +=  kolona[0]+"."+kolona[1] + ", ";
            }


        }
        query = query.substring(0, query.length() - 2);// Makni zadnji zarez
        query +=" FROM " + tablica ;
        for (int i = 0; i < join.length; i++) {


//            query += " LEFT JOIN " + join[i][2] + " ON " + join[i][0]+ "." + join[i][1] + " = " + join[i][2]+ "." + join[i][3];

//            query += " LEFT JOIN " + join[i][2] + " AS " + join[i][1] + " ON " + join[i][2]+ "." + join[i][1] + " = " + join[i][1]+ "." + join[i][3];
//
//            if (naziv_kolone.equals(referentna_kolona)) {
//                query += " LEFT JOIN " + join[i][2] + " ON " + join[i][0] + "." + join[i][1] + " = " + join[i][2] + "." + join[i][3];
//            } else {
//                query += " LEFT JOIN " + referentna_tablica + " AS " + referentna_tablica + " ON " + referentna_tablica + "." + referentna_kolona + " = " + naziv_tablice + "." + naziv_kolone;
//            }

            String naziv_tablice  = join[i][0];
            String naziv_kolone  = join[i][1];
            String referentna_tablica  = join[i][2];
            String referentna_kolona  = join[i][3];
            if (naziv_tablice.equals(referentna_tablica)) {
                query += " LEFT JOIN " + referentna_tablica + " AS " + referentna_kolona + " ON " + referentna_kolona + "." + referentna_kolona + " = " + naziv_tablice + "." + naziv_kolone;
            } else {
                query += " LEFT JOIN " + referentna_tablica + " AS " + referentna_tablica + " ON " + referentna_tablica + "." + referentna_kolona + " = " + naziv_tablice + "." + naziv_kolone;
            }

        }

        return query;
    }


    public static String[][] getQueryFields(String query) throws SQLException {
        Connection conn = getConnection();

        // Get column names for the specified table
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();
        boolean imaPrvi = false;
        String[][] columnNames = new String[columnCount][2];
        String[] tableNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1][0] = metadata.getTableName(i);
            columnNames[i - 1][1] = metadata.getColumnName(i);
        }
        return columnNames;
    }

    public static String[] filtrirajJedinstveneKolone(String[] kolone) {
        // Kreiraj LinkedHashSet za jedinstvene vrijednosti kolona
        LinkedHashSet<String> jedinstveneKolone = new LinkedHashSet<>();

        // Dodaj sve kolone u LinkedHashSet
        for (String kolona : kolone) {
            jedinstveneKolone.add(kolona);
        }

        // Vrati jedinstvene kolone kao rezultat
        return jedinstveneKolone.toArray(new String[0]);
    }

    public static String getQueryForJoinTables(String tablica) throws SQLException {
        String[][] join = getJoinData(tablica);
        String query = getJoinQuery(tablica,join);
//        System.out.println(query);
        String[][] columns = getQueryFields(query);
        query = getFinalJoinQuery(tablica,columns,join);
//        System.out.println(query);
        return query;
    }

    public static String[] getDatabaseNames(String port, String username, String password) {
        String url = "jdbc:mysql://localhost:" + port;

        try {
            // Spajanje na MySQL server
            Connection connection = DriverManager.getConnection(url, username, password);

            // Izvršavanje SQL upita za dobivanje liste svih baza podataka
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getCatalogs();

            // Prikupljanje imena svih baza podataka u niz
            int size = 0;
            while (resultSet.next()) {
                size++;
            }
            String[] databaseNames = new String[size];
            resultSet.beforeFirst();
            int i = 0;
            while (resultSet.next()) {
                String databaseName = resultSet.getString("TABLE_CAT");
                databaseNames[i++] = databaseName;
            }

            // Zatvaranje resursa
            resultSet.close();
//            connection.close();

            return databaseNames;
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[0];
        }
    }
}
