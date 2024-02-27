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
    private boolean fetchOnnistui = false;
    ErrorEmail errorEmail = new ErrorEmail();

    public ProxyPalvelu() {
        this.airportList = new ArrayList<>();
    }

    public void aja() {
        int yritykset = 0;

        while (!fetchOnnistui && yritykset < 2) {
            try {
                Document doc = Jsoup.connect("https://www.kanasair.fi/category/10/ilmailupolttoaineet--aviation-fuel")
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
                                    } else {
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
                kirjoitaLokiin("Tietojen haku onnistui.");
                errorEmail.resetCounter();
                fetchOnnistui = true;
            } catch (Exception e) {
                System.out.println("Virhe haettaessa tietoja: " + e.getMessage());
                kirjoitaLokiin("Virhe haettaessa tietoja: " + e.getMessage());
                yritykset++;
            }
        }
        if (!fetchOnnistui) {
            System.out.println("Tietojen haku epäonnistui.");
            kirjoitaLokiin("Tietojen haku epäonnistui.");
            errorEmail.addError();
        }
    }

    private void kirjoitaLokiin(String viesti) {
        LocalDateTime aikaleima = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String aikaString = aikaleima.format(formatter);
        try (FileWriter writer = new FileWriter("lokiteksti.txt", true)) {
            writer.write("[" + aikaString + "] " + viesti + "\n");
        } catch (IOException e) {
            System.out.println("Virhe lokitiedoston kirjoittamisessa: " + e.getMessage());
        }
    }

    private void tallennaJsonTiedostoon(String data, String tiedosto) {
        try (FileWriter writer = new FileWriter(tiedosto)) {
            writer.write(data);
        } catch (IOException e) {
            System.out.println("Virhe tallennettaessa JSON-dataa tiedostoon: " + e.getMessage());
        }
    }

    public String generateJsonOutput() {
        if (!fetchOnnistui) {
            System.out.println("Tietoja ei päivitetty JSON-tiedostoihin, koska tietojen hakeminen epäonnistui.");
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

        tallennaJsonTiedostoon(jsonData, "data.json");
        tallennaJsonTiedostoon(jsonData, "data.json.bak");

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