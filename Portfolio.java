import java.util.*;

public class Portfolio implements Observable<String> {
    private final String portfolioId;
    private final String ownerName;
    private final List<Position> positions;
    private final List<Observer<String>> observers;

    public Portfolio(String portfolioId, String ownerName) {
        this.portfolioId = portfolioId;
        this.ownerName = ownerName;
        this.positions = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public void addPosition(Instrument inst, int qty, double costBasis) {
        if (qty < 0 || costBasis < 0) return;
        for (Position pos : positions) {
            if (pos.getInstrument().getSymbol().equals(inst.getSymbol())) {
                pos.addQuantity(qty, costBasis);
                this.notifyObservers("ADDED: " + inst.getSymbol() + " x" + qty);
                return;
            }
        }
        positions.add(new Position(inst, qty, costBasis));

        this.notifyObservers("ADDED: " + inst.getSymbol() + " x" + qty);
    }

    public void removePosition(String symbol) throws PositionNotFoundException {
        Position toRemove = null;

        for (Position pos : positions) {
            if (pos.getInstrument().getSymbol().equals(symbol)) {
                toRemove = pos;
                break;
            }
        }

        positions.remove(toRemove);
        throw new PositionNotFoundException("Not found!");
    }

    public double totalMarketValue() {
        double total = 0;
        for (Position pos : positions) {
            total += pos.marketValue();
        }

        return total;
    }

    public double totalUnrealizedPnL() {
        double total = 0;
        for (Position pos : positions) {
            total += pos.unrealizedPnL();
        }

        return total;
    }

    public Position getPosition(String symbol) throws PositionNotFoundException {
        for (Position pos : positions) {
            if (pos.getInstrument().getSymbol().equals(symbol)) {
                return pos;
            }
        }       

        throw new PositionNotFoundException("Not found!");
    }

    public List<Position> getPositionsSortedByValue() {
        List<Position> descendingSortedPositionValue = new ArrayList<>(this.positions   );

        //sort đc implement bằng Timsort
        descendingSortedPositionValue.sort((p1, p2) -> Double.compare(p2.marketValue(), p1.marketValue()));

        return descendingSortedPositionValue;
    }

    public Map<String, Double> allocationByAssetClass() {
        Map<String, Double> assetPercentage = new HashMap<>();
        double totalMarketValue = totalMarketValue();

        for (Position pos : positions) {
            String asset = pos.getInstrument().assetClass();
            Double assetValue = pos.marketValue();

            assetPercentage.put(asset, assetValue);
        }

        for (Map.Entry<String, Double> entry : assetPercentage.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / totalMarketValue;
            assetPercentage.put(entry.getKey(), percentage);
        }

        return assetPercentage;
    }

    public void revalueAll(PricingStrategy strategy) {
        if (positions.isEmpty()) return;

        for (Position pos : positions) {
            pos.getInstrument().updatePrice(strategy.calculateFairValue(pos.getInstrument()));
        }

        this.notifyObservers("REVALUED: " + strategy.strategyName());
    }

    @Override
    public void addObserver(Observer<String> observer) {
        if (!observers.contains(observer)) observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<String> observer) {
        if (observers.contains(observer)) observers.remove(observer);
    }

    @Override
    public void notifyObservers(String event) {
        observers.forEach(o -> o.onEvent(event));
    }

    public String getPortfolioId() {
        return this.portfolioId;
    }

    public String getOwnerName() {
        return this.ownerName;
    }
}
