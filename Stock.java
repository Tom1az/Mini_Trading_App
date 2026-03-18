public class Stock extends Instrument {
    private final double marketCap;
    private final String sector;

    public Stock(String symbol, String name, double currentPrice, double marketCap, String sector) {
        super(symbol, name, currentPrice);
        // TODO
        this.marketCap = marketCap;
        this.sector = sector;
    }

    @Override
    public double riskScore() {
        // TODO
        if (this.marketCap < 1e9) {
            return 7.5;
        } else if (this.marketCap < 1e10) {
            return 5.0;
        } else {
            return 3.0;
        }
    }

    @Override
    public String assetClass() {
        // TODO
        return "EQUITY";
    }
    
    public double getMarketCap() {
        // TODO
        return this.marketCap;
    }

    public String getSector() {
        // TODO
        return this.sector;
    }
}
