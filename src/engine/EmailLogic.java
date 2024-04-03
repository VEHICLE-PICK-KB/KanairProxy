

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class EmailLogic {

    private String errorCountString = "";

    String logContent = readLastLines("/var/www/10/jtjuslin/sites/jtjuslin.kapsi.fi/www/kananen/log.txt", 10);
    String emailBody = "Error occurred! Last 10 lines of log:\n" + logContent;

    private void readFile() {
        File file = new File("/var/www/10/jtjuslin/sites/jtjuslin.kapsi.fi/www/kananen/javat/errorCounter.txt");
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
            EmailSender.sendEmail(emailBody);
            resetCounter();
        }
    }

    public void resetCounter() {
        updateCounter("1");
    }

    private void updateCounter(String errorCountString) {
        try (FileWriter writer = new FileWriter("/var/www/10/jtjuslin/sites/jtjuslin.kapsi.fi/www/kananen/javat/errorCounter.txt", false)) {
            writer.write(errorCountString);
        } catch (IOException e) {
            System.out.println("Error while counting: " + e.getMessage());
        }
    }

    private static String readLastLines(String filePath, int linesToRead) {
    LinkedList<String> lastLines = new LinkedList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;

        while ((line = reader.readLine()) != null) {
            if (lastLines.size() == linesToRead) {
                lastLines.poll(); // Remove the first line to keep the size fixed
            }
            lastLines.add(line); // Add the new line
        }
    } catch (IOException e) {
        System.out.println("Error reading log file: " + e.getMessage());
    }

    StringBuilder result = new StringBuilder();
    for (String lastLine : lastLines) {
        result.append(lastLine).append("\n");
    }
    return result.toString();
}
}