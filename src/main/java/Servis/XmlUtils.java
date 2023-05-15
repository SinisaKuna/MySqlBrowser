package Servis;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.sql.*;
import java.io.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import static Servis.DatabaseConnection.getConnection;

public class XmlUtils {


    public static void kreirajXMLDatoteku(String naziv_tablice, String putanja_datoteke) {
        try {
            // Spajanje na bazu podataka
            Connection conn = getConnection();

            // Izvršavanje SQL upita za dohvat podataka iz tablice
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM " + naziv_tablice;
            ResultSet rs = stmt.executeQuery(query);

            // Kreiranje XML datoteke i elemenata
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement(naziv_tablice);
            doc.appendChild(rootElement);

            // Prolazak kroz rezultate upita i stvaranje XML elemenata za svaki redak
            while (rs.next()) {
                Element row = doc.createElement("row");
                rootElement.appendChild(row);

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);

                    Element column = doc.createElement(columnName);
                    column.appendChild(doc.createTextNode(value.toString()));
                    row.appendChild(column);
                }
            }

            // Pisanje XML datoteke na disk
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(putanja_datoteke));
            transformer.transform(source, result);

            // Zatvaranje veze s bazom podataka
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        kreirajXMLDatoteku("osobe", "xml/osobe.xml");
    }


}
