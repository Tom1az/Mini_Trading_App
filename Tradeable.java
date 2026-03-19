public interface Tradeable {
    String getSymbol();

    double getCurrentPriceValue();

    boolean isAvailableForTrading();

    default String getTradingInfo() {
        return "Tradeable: %s at %.2f (Available)".formatted(this.getSymbol(), this.getCurrentPriceValue());
    }
}
