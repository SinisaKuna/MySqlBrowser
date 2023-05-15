package Servis;
import java.sql.*;
import java.util.*;

import static Servis.DatabaseConnection.getConnection;
import static Servis.MySQLHelper.*;

public class Testing {

    public static void main(String[] args) throws SQLException {
        String tablica = "osoba";
        String[][] join = getJoinData(tablica);
        String query = getJoinQuery(tablica,join);
        System.out.println(query);
        System.out.println("-----------------");
        String[][] columns = getQueryFields(query);
                for (String[] redak : columns) {
                    System.out.println(redak[0]+"."+redak[1]);
                }

        System.out.println("-----------------");
        //String[] kolone = filtrirajJedinstveneKolone(columns);

        //query = getFinalJoinQuery(tablica,kolone,join);
        //System.out.println(query);



//        String tablica = "postolar";
//        String[][] join = getJoinData(tablica);
//        String query = getJoinQuery(tablica,join);
//        System.out.println(query);
//        String[] columns = getQueryFields(query);
//        for (String kolona : columns) {
//            System.out.println(kolona);
//        }
//        String[] kolone = filtrirajJedinstveneKolone(columns);
//        System.out.println("-------------------");
//        for (String kolona : kolone) {
//            System.out.println(kolona);
//        }
//
//        query = getFinalJoinQuery(tablica,kolone,join);

//
//        String query = getQueryForJoinTables("postolar");
//        System.out.println(query);

    }


}
