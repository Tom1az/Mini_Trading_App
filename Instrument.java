import java.time.LocalDateTime;

public abstract class Instrument implements Tradeable, Priceable {
    private final String symbol;
    private String name;
    private double currentPrice;
    private LocalDateTime lastUpdated;

    public Instrument(String symbol, String name, double currentPrice) {
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
        this.lastUpdated = null;
    }

    public abstract double riskScore();

    public abstract String assetClass();
    
    public abstract void accept(InstrumentVisitor visitor);
    
    public void updatePrice(double newPrice) {
        if (newPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.currentPrice = newPrice;
        this.lastUpdated = LocalDateTime.now();
    }

    @Override
    public String getSymbol() {
        return this.symbol;
    }
    
    public String getName() {
        return this.name;
    }

    @Override
    public double getCurrentPriceValue() {
        return this.currentPrice;
    }

    public LocalDateTime getLastUpdated() {
        return this.lastUpdated;
    }

    @Override
    public String toString() {
        return "%s[symbol=%s, price=%s, risk=%s]".formatted(this.getClass().getSimpleName(), this.symbol, this.currentPrice, this.riskScore());
    }

    @Override
    public double getPriceChange(double previousPrice) {
        return this.currentPrice - previousPrice;
    }

    @Override
    public double getPriceChangePercent(double previousPrice) {
        return ((this.currentPrice - previousPrice) / previousPrice) * 100;
    }

    @Override
    public boolean isAvailableForTrading() {
        return true;
    }
}
