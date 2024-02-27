package engine;

public class FuelPrices {
    private String avgas;
    private String jetA1;
    private String mogas;

    public FuelPrices(String avgas, String jetA1, String mogas) {
        this.avgas = avgas;
        this.jetA1 = jetA1;
        this.mogas = mogas;
    }

    public String getAvgas() {
        return avgas;
    }

    public String getJetA1() {
        return jetA1;
    }

    public String getMogas() {
        return mogas;
    }
}
