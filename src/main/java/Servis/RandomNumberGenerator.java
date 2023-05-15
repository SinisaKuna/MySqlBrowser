package Servis;
import java.util.Random;

public class RandomNumberGenerator {
    public static int generateRandomNumber() {
        Random rand = new Random();
        int randomNumber = rand.nextInt(9000) + 1000;
        return randomNumber;
    }

    public static void main(String[] args) {
        int random = generateRandomNumber();
        System.out.println("Slučajni broj: " + random);
    }
}
