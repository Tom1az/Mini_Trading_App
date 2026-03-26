public interface Tradeable {
    String getSymbol();

    double getCurrentPriceValue();

    boolean isAvailableForTrading();

    default String getTradingInfo() {
        if (!this.isAvailableForTrading()) {
            return "%s @ %.2f [UNAVAILABLE]".formatted(this.getSymbol(), this.getCurrentPriceValue());
        }
        return "%s @ %.2f [AVAILABLE]".formatted(this.getSymbol(), this.getCurrentPriceValue());
    }
}
