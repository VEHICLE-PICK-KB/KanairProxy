package engine;

import java.util.Map;

public class Airport {
    private String pouserStatus;
    private String airportCode;
    private Map<String, String> fuelPrices;

    public Airport(String pouserStatus, String airportCode, Map<String, String> fuelPrices) {
        this.pouserStatus = pouserStatus;
        this.airportCode = airportCode;
        this.fuelPrices = fuelPrices;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public String getPouserStatus() {
        return pouserStatus;
    }

    public Map<String, String> getFuelPrices() {
        return fuelPrices;
    }
}