public class Bond extends Instrument {
    private final double couponRate;
    private final int maturityYears;

    public Bond(String symbol, String name, double currentPrice, double couponRate, int maturityYears) {
        super(symbol, name, currentPrice);
        // TODO
        this.couponRate = couponRate;
        this.maturityYears = maturityYears;
    }

    @Override
    public double riskScore() {
        // TODO
        if (this.maturityYears > 10) {
            return 4.0;
        } else {
            return 2.0;
        }
    }

    @Override
    public String assetClass() {
        // TODO
        return "FIXED_INCOME";
    }

    public double annualCouponPayment(int units) {
        // TODO
        return units * this.getCurrentPriceValue() * this.couponRate / 100.0;
    }

    public double getCouponRate() {
        // TODO
        return this.couponRate;
    }

    public int getMaturityYears() {
        // TODO
        return this.maturityYears;
    }
}
