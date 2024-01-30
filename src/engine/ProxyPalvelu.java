package engine;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ProxyPalvelu {

    private List<Airport> airportList;

    public ProxyPalvelu() {
        this.airportList = new ArrayList<>();
    }

    public void aja() {
        try {
            Document doc = Jsoup.connect("https://www.kanair.fi/category/10/ilmailupolttoaineet--aviation-fuel").get();

            Elements rows = doc.select("table tbody tr");
            for (Element row : rows) {
                Elements columns = row.select("td");
                if (columns.size() >= 6) {
                    String pouserStatus = columns.get(0).text().trim();
                    String airportCode = columns.get(1).text().trim();
                    String avgasPrice = columns.get(2).text().trim().replace("\"", "").replace(",", ".");
                    String jetA1Price = columns.get(3).text().trim().replace("\"", "").replace(",", ".");
                    String mogasPrice = columns.get(4).text().trim().replace("\"", "").replace(",", ".");

                    if (avgasPrice.equalsIgnoreCase("n/a") || !avgasPrice.matches("-?\\d+(\\.\\d+)?")) {
                        avgasPrice = "NA";
                    }
                    if (jetA1Price.equalsIgnoreCase("n/a") || !jetA1Price.matches("-?\\d+(\\.\\d+)?")) {
                        jetA1Price = "NA";
                    }
                    if (mogasPrice.equalsIgnoreCase("n/a") || !mogasPrice.matches("-?\\d+(\\.\\d+)?")) {
                        mogasPrice = "NA";
                    }

                    FuelPrices fuelPrices = new FuelPrices(avgasPrice, jetA1Price, mogasPrice);
                    Airport airport = new Airport(pouserStatus, airportCode, fuelPrices);
                    airportList.add(airport);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateJsonOutput() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(airportList);
        System.out.println(jsonOutput);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Arkipaivareporter");
        ProxyPalvelu olio = new ProxyPalvelu();
        olio.aja();
        olio.generateJsonOutput();
    }
}
