

import java.time.LocalDateTime;
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
    //private CustomWebBotHandler customWebBotHandler;

    public ProxyPalvelu() {
        this.airportList = new ArrayList<>();
        //this.customWebBotHandler = new CustomWebBotHandler();

    }

    public void aja() {
        try {
            Document doc = Jsoup.connect("https://www.kanair.fi/category/10/ilmailupolttoaineet--aviation-fuel").get();

            Elements rows = doc.select("table tbody tr");

            // Extract header row to get titles. Adapts to changes in the table structure
            Elements headerRow = doc.select("table th");
            List<String> titles = new ArrayList<>();
            for (int i = 2; i < headerRow.size(); i++) {
                try {
                    String title = headerRow.get(i).text().trim().toUpperCase();
                    titles.add(title);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Index out of bounds while extracting titles");
                    e.printStackTrace();
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
                                }
                                else {
                                    // Lisää tyhjä hinta, jos tietoja ei ole saatavilla
                                    fuelPricesMap.put(fuelType, "NA");
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds while processing row: " + row);
                            e.printStackTrace();
                        }
                    }

                    Airport airport = new Airport(pouserStatus, airportCode, fuelPricesMap);
                    airportList.add(airport);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generateJsonOutput() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        LocalDateTime date = LocalDateTime.now();
        TimeStamp timeStamp = new TimeStamp(date);

        JsonOutput jsonOutput = new JsonOutput(timeStamp, airportList);
        return gson.toJson(jsonOutput);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Arkipaivareporter");
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