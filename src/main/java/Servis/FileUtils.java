package Servis;

import java.io.*;

public class FileUtils {
    public static void writeToTxt(String[] podaci, String filePath) {
        try {
            FileWriter myWriter = new FileWriter(filePath);
            for (int i = 0; i < podaci.length; i++) {
                myWriter.write(podaci[i] + "\n");
            }
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public static void deleteTxt(String filePath) {
        try {
            PrintWriter pw = new PrintWriter(filePath);
            pw.close();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    public static String[] readTxt(String filePath, int rowNum) throws IOException {
        FileReader file = new FileReader(filePath);
        BufferedReader reader = new BufferedReader(file);
        String[] podaci = new String[rowNum];

        String line = reader.readLine();

        for (int i = 0; i < podaci.length; i++) {
            podaci[i] = line;
            line = reader.readLine();
        }
        return podaci;
    }
}
