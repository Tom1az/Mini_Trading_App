public class Position {
    private final Instrument instrument;
    private int quantity;
    private double averageCostBasis;

    public Position(Instrument instrument, int quantity, double averageCostBasis) {
        this.instrument = instrument;
        this.quantity = quantity;
        this.averageCostBasis = averageCostBasis;
    }

    public double marketValue() {
        return this.quantity * instrument.getCurrentPriceValue();
    }

    public double unrealizedPnL() {
        return this.marketValue() - this.quantity * this.averageCostBasis;
    }   

    public void addQuantity(int qty, double costBasis) {
        if (qty < 0 || costBasis < 0 || (qty == 0 && costBasis == 0)) return;
        this.averageCostBasis = (this.quantity * this.averageCostBasis + qty * costBasis) / (this.quantity + qty);
        this.quantity += qty;
    }

    public Instrument getInstrument() {
       return this.instrument;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public double getAverageCostBasis() {
        return this.averageCostBasis;
    }

    @Override
    public String toString() {
       return "";
    }
}
