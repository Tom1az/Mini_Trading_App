import java.time.LocalDateTime;

public abstract class Instrument {
    private final String symbol;
    private String name;
    private double currentPrice;
    private LocalDateTime lastUpdated;

    public Instrument(String symbol, String name, double currentPrice) {
        // TODO
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
        this.lastUpdated = null;
    }

    public abstract double riskScore();

    public abstract String assetClass();
    
    public void updatePrice(double newPrice) {
        // TODO
        if (newPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.currentPrice = newPrice;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getSymbol() {
        // TODO
        return this.symbol;
    }

    public String getName() {
        // TODO
        return this.name;
    }

    public double getCurrentPriceValue() {
        // TODO
        return this.currentPrice;
    }

    public LocalDateTime getLastUpdated() {
        // TODO
        return this.lastUpdated;
    }

    @Override
    public String toString() {
        // TODO
        return "Instrument[symbol = %s, price = %.2f, risk = %.2f]".formatted(this.symbol, this.currentPrice, this.riskScore());
    }
}
