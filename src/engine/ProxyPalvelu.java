package engine;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ProxyPalvelu {

    private List<Airport> airportList;
    private boolean fetchSuccessful = false;

    // Constructor initializes the list.
    public ProxyPalvelu() {
        this.airportList = new ArrayList<>();
    }

    // Main method to fetch data and process it.
    public void aja() {

        EmailLogic emailLogic = new EmailLogic();

        try {

            //Connects to kanair and fetches its HTML document.
            Document doc = Jsoup.connect("https://www.kanair.fi/category/10/ilmailupolttoaineet--aviation-fuel")
                    .get();
            airportList.clear();
            Elements rows = doc.select("table tbody tr");

            // Extracting table headers for fuel types.
            Elements headerRow = doc.select("table th");
            List<String> titles = new ArrayList<>();
            for (int i = 2; i < headerRow.size(); i++) {
                try {
                    String title = headerRow.get(i).text().trim().toUpperCase();
                    titles.add(title);
                } catch (IndexOutOfBoundsException e) {
                    // Logging errors encountered while extracting titles.
                    System.out.println("Index out of bounds while extracting titles" + e.getMessage());
                    updateLog("Index out of bounds while extracting titles" + e.getMessage());
                }
            }

            // Iterating over each row to fetch airport and fuel price data.
            for (Element row : rows) {
                Elements columns = row.select("td");
                if (columns.size() >= 6) {  // Ensures the row has a sufficient number of columns.
                    String pouserStatus = columns.get(0).text().trim();
                    String airportCode = columns.get(1).text().trim();

                    // Skips rows with invalid airport codes.
                    if (!airportCode.matches("[A-Za-z0-9]+")) {
                        continue;
                    }

                    // Mapping fuel types to their prices.
                    Map<String, String> fuelPricesMap = new HashMap<>();
                    for (int i = 2; i < columns.size(); i++) {
                        try {
                            String fuelType = titles.get(i - 2);
                            String fuelPrice = columns.get(i).text().trim().replace("\"", "").replace(",", ".");

                            // Checks for valid fuel prices and marks unavailable prices as "NA".
                            if (!fuelType.isEmpty()) {
                                if (fuelPrice.matches("-?\\d+(\\.\\d+)?") || !fuelPrice.equalsIgnoreCase("n/a")) {
                                    fuelPrice = fuelPrice.equals("-") ? "NA" : fuelPrice;
                                    fuelPricesMap.put(fuelType, fuelPrice);
                                } else {
                                    fuelPricesMap.put(fuelType, "NA");
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {
                            // Logging errors encountered during row processing.
                            updateLog("Index out of bounds while processing row: " + row + ". " + e.getMessage());
                        }
                    }

                    // Creates an Airport object and adds it to the list.
                    Airport airport = new Airport(pouserStatus, airportCode, fuelPricesMap);
                    airportList.add(airport);
                }
            }
            updateLog("Fetch was successful."); // Log success.
            fetchSuccessful = true;
        } catch (Exception e) {

            // Log any exceptions caught during the fetch operation.
            System.out.println("Error while fetching data: " + e.getMessage());
            updateLog("Error while fetching data: " + e.getMessage());
        }

        // Checks if the fetch was unsuccessful and updates email logic accordingly.
        if (!fetchSuccessful) {
            emailLogic.addError();
        } else {
            emailLogic.resetCounter();
        }
    }

    // Method to log messages with a timestamp.
    public void updateLog(String message) {
        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timeString = timestamp.format(formatter);

        try (FileWriter writer = new FileWriter("/var/www/10/jtjuslin/sites/jtjuslin.kapsi.fi/www/kananen/log.txt", true)) {
            
           // Writes log message with timestamp.
            writer.write("[" + timeString + "] " + message + "\n");

            // Retrieves and logs the stack trace to help trace the source of the message.
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace.length > 2) {
                StackTraceElement element = stackTrace[2];
                writer.write("   at " + element.getClassName() + "." + element.getMethodName() +
                        "(" + element.getFileName() + ":" + element.getLineNumber() + ")\n");
            }
        } catch (IOException e) {

            // Logs to console if there's an error writing to the log file.
            System.out.println("Error while logging: " + e.getMessage());
        }
    }

    // Method to save JSON representation of data into a file.
    private void saveJsonInFile(String data, String file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data);
        } catch (IOException e) {
            System.out.println("Error while saving the Json object to a file: " + e.getMessage());
        }
    }

    // Generates JSON output from the current data and saves it.
    public String generateJsonOutput() {
        if (!fetchSuccessful) {
            System.out.println("Data wasn't updated, fetch was unsuccessful");
            return "";
        }

        // Configures Gson for pretty printing and registers a custom adapter for
        // LocalDateTime.

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        LocalDateTime date = LocalDateTime.now();
        TimeStamp timeStamp = new TimeStamp(date);

        // Creates a JSON output object containing timestamps and airport list.
        JsonOutput jsonOutput = new JsonOutput(timeStamp, airportList);
        String jsonData = gson.toJson(jsonOutput);

        // Add headers
        String header = "Content-Type: application/json\n"
        + "Access-Control-Allow-Origin: *\n"
        + "Access-Control-Allow-Methods: GET, POST, OPTIONS\n"
        + "Access-Control-Allow-Headers: Content-Type\n";
        
        System.out.println(header + jsonData);

        // Saves the JSON data into two files, a primary and a backup.
        saveJsonInFile(jsonData, "/var/www/10/jtjuslin/sites/jtjuslin.kapsi.fi/www/kananen/data.json");
        saveJsonInFile(jsonData, "/var/www/10/jtjuslin/sites/jtjuslin.kapsi.fi/www/kananen/data.json.bak");

        return header + jsonData;
    }

    // Main method to run the scraping process and generate JSON output.
    public static void main(String[] args) {
        ProxyPalvelu olio = new ProxyPalvelu();
        olio.aja(); // Starts the data fetching process.
        String jsonOutput = olio.generateJsonOutput(); // Generates JSON from the fetched data.
        System.out.println(jsonOutput); // Prints the JSON output to console.
    }

    // Inner class to structure the JSON output.
    static class JsonOutput {
        private TimeStamp timeStamp;
        private List<Airport> airportList;

        public JsonOutput(TimeStamp timeStamp, List<Airport> airportList) {
            this.timeStamp = timeStamp;
            this.airportList = airportList;
        }

        // Getter methods for accessing properties.
        public List<Airport> getAirportList() {
            return this.airportList;
        }

        public TimeStamp getTimeStamp() {
            return this.timeStamp;
        }
    }
}