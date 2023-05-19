package Servis;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryGenerator {
    private Connection connection;

    public QueryGenerator(Connection connection) {
        this.connection = connection;
    }

//    public String generateQuery(String tableName) throws SQLException {
//        StringBuilder queryBuilder = new StringBuilder();
//        List<String> joinClauses = new ArrayList<>();
//
//        // Dohvati sve strane ključeve za daniu tablicu
//        DatabaseMetaData metaData = connection.getMetaData();
//        ResultSet foreignKeys = metaData.getImportedKeys(connection.getCatalog(), null, tableName);
//
//        // Generiraj JOIN-ove za svaki strani ključ
//        while (foreignKeys.next()) {
//            String pkTableName = foreignKeys.getString("PKTABLE_NAME");
//            String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
//            String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
//
//            String joinClause = "JOIN " + pkTableName + " ON " +
//                    tableName + "." + fkColumnName + " = " +
//                    pkTableName + "." + pkColumnName;
//
//            joinClauses.add(joinClause);
//        }
//
//        // Generiraj SELECT upit za sve polja tablice i JOIN-ove
//        queryBuilder.append("SELECT ").append(tableName).append(".*");
//        for (String joinClause : joinClauses) {
//            queryBuilder.append(", ").append(joinClause);
//        }
//        queryBuilder.append(" FROM ").append(tableName);
//        for (String joinClause : joinClauses) {
//            queryBuilder.append(" ").append(joinClause);
//        }
//
//        return queryBuilder.toString();
//    }
public String generateQuery(String tableName) throws SQLException {
    StringBuilder queryBuilder = new StringBuilder();
    List<String> joinClauses = new ArrayList<>();

    // Dohvati sve strane ključeve za daniu tablicu
    DatabaseMetaData metaData = connection.getMetaData();
    ResultSet foreignKeys = metaData.getImportedKeys(connection.getCatalog(), null, tableName);

    // Generiraj JOIN-ove za svaki strani ključ
    while (foreignKeys.next()) {
        String pkTableName = foreignKeys.getString("PKTABLE_NAME");
        String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
        String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");

        String joinClause = "JOIN " + pkTableName + " ON " +
                tableName + "." + fkColumnName + " = " +
                pkTableName + "." + pkColumnName;

        joinClauses.add(joinClause);
    }

    // Generiraj SELECT upit za sve polja tablice i JOIN-ove
    queryBuilder.append("SELECT * FROM ").append(tableName);
    for (String joinClause : joinClauses) {
        queryBuilder.append(" ").append(joinClause);
    }

    return queryBuilder.toString();
}

}

