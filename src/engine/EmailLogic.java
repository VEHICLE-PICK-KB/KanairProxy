package engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EmailLogic {

    private String errorCountString = "";

    private void readFile() {
        File file = new File("counter.txt");
        int ch;

        FileReader fr = null;
        try {
            fr = new FileReader(file);
            StringBuilder stringBuilder = new StringBuilder();

            while ((ch = fr.read()) != -1) {
                stringBuilder.append((char) ch);
            }
            errorCountString = stringBuilder.toString();

        } catch (FileNotFoundException exception) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error reading the file");
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing the file");
            }
        }
    }

    public void addError() {
        readFile();
        int errorCount = Integer.parseInt(errorCountString) + 1;
        updateCounter(String.valueOf(errorCount));

        if (errorCount == 96) {
            EmailSender.sendEmail();
            resetCounter();
        }
    }

    public void resetCounter() {
        updateCounter("95");
    }

    private void updateCounter(String errorCountString) {
        try (FileWriter writer = new FileWriter("counter.txt", false)) {
            writer.write(errorCountString);
        } catch (IOException e) {
            System.out.println("Virhe laskiessa: " + e.getMessage());
        }
    }
}