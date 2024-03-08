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

    public ProxyPalvelu() {
        this.airportList = new ArrayList<>();
    }

    public void aja() {

        EmailLogic emailLogic = new EmailLogic();

        try {
            Document doc = Jsoup.connect("https://www.kanair.fi/category/10/ilmailupolttoaineet--aviation-fuel")
                    .get();
            airportList.clear();
            Elements rows = doc.select("table tbody tr");

            Elements headerRow = doc.select("table th");
            List<String> titles = new ArrayList<>();
            for (int i = 2; i < headerRow.size(); i++) {
                try {
                    String title = headerRow.get(i).text().trim().toUpperCase();
                    titles.add(title);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Index out of bounds while extracting titles" + e.getMessage());
                    updateLog("Index out of bounds while extracting titles" + e.getMessage());
                }
            }

            for (Element row : rows) {
                Elements columns = row.select("td");
                if (columns.size() >= 6) {
                    String pouserStatus = columns.get(0).text().trim();
                    String airportCode = columns.get(1).text().trim();

                    if (!airportCode.matches("[A-Za-z0-9]+")) {
                        continue;
                    }

                    Map<String, String> fuelPricesMap = new HashMap<>();
                    for (int i = 2; i < columns.size(); i++) {
                        try {
                            String fuelType = titles.get(i - 2);
                            String fuelPrice = columns.get(i).text().trim().replace("\"", "").replace(",", ".");

                            if (!fuelType.isEmpty()) {
                                if (fuelPrice.matches("-?\\d+(\\.\\d+)?") || !fuelPrice.equalsIgnoreCase("n/a")) {
                                    fuelPrice = fuelPrice.equals("-") ? "NA" : fuelPrice;
                                    fuelPricesMap.put(fuelType, fuelPrice);
                                } else {
                                    fuelPricesMap.put(fuelType, "NA");
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {
                            updateLog("Index out of bounds while processing row: " + row + ". " + e.getMessage());
                        }
                    }

                    Airport airport = new Airport(pouserStatus, airportCode, fuelPricesMap);
                    airportList.add(airport);
                }
            }
            updateLog("Fetch was successful.");
            fetchSuccessful = true;
        } catch (Exception e) {
            System.out.println("Error while fetching data: " + e.getMessage());
            updateLog("Error while fetching data: " + e.getMessage());
        }

        if (!fetchSuccessful) {
            emailLogic.addError();
        } else {
            emailLogic.resetCounter();
        }
    }

    public void updateLog(String message) {
        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timeString = timestamp.format(formatter);

        try (FileWriter writer = new FileWriter("log.txt", true)) {
            writer.write("[" + timeString + "] " + message + "\n");
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace.length > 2) {
                StackTraceElement element = stackTrace[2];
                writer.write("   at " + element.getClassName() + "." + element.getMethodName() +
                        "(" + element.getFileName() + ":" + element.getLineNumber() + ")\n");
            }
        } catch (IOException e) {
            System.out.println("Error while logging: " + e.getMessage());
        }
    }

    private void saveJsonInFile(String data, String file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data);
        } catch (IOException e) {
            System.out.println("Error while saving the Json object to a file: " + e.getMessage());
        }
    }

    public String generateJsonOutput() {
        if (!fetchSuccessful) {
            System.out.println("Data wasn't updated, fetch was unsuccessful");
            return "";
        }

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        LocalDateTime date = LocalDateTime.now();
        TimeStamp timeStamp = new TimeStamp(date);

        JsonOutput jsonOutput = new JsonOutput(timeStamp, airportList);
        String jsonData = gson.toJson(jsonOutput);

        saveJsonInFile(jsonData, "data.json");
        saveJsonInFile(jsonData, "data.json.bak");

        return jsonData;
    }

    public static void main(String[] args) {
        ProxyPalvelu olio = new ProxyPalvelu();
        olio.aja();
        String jsonOutput = olio.generateJsonOutput();
        System.out.println(jsonOutput);
    }

    static class JsonOutput {
        private TimeStamp timeStamp;
        private List<Airport> airportList;

        public JsonOutput(TimeStamp timeStamp, List<Airport> airportList) {
            this.timeStamp = timeStamp;
            this.airportList = airportList;
        }

        public List<Airport> getAirportList() {
            return this.airportList;
        }

        public TimeStamp getTimeStamp() {
            return this.timeStamp;
        }
    }
}