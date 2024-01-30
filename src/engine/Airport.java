package engine;

public class Airport {
    private String pouserStatus;
    private String airportCode;
    private FuelPrices fuels;

    public Airport(String pouserStatus, String airportCode, FuelPrices fuels) {
        this.pouserStatus = pouserStatus;
        this.airportCode = airportCode;
        this.fuels = fuels;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public String getPouserStatus() {
        return pouserStatus;
    }

    public FuelPrices getFuels() {
        return fuels;
    }
}
