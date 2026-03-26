import java.util.*;

public class RiskAnalyzer<T extends Instrument> {
    private final List<T> instruments = new ArrayList<>();

    public void add(T instrument) {
        T oldInstrument = null;

        for (T i : instruments) {
            if (i.getSymbol().equals(instrument.getSymbol())) {
                oldInstrument = i;
                break;
            }
        }

        if (oldInstrument != null) {
            instruments.remove(oldInstrument);
            instruments.add(instrument);
        } else instruments.add(instrument);
    }

    public void removeInstrument(T instrument) {
        T toRemove = null;

        for (T i : instruments) {
            if (i.getSymbol().equals(instrument.getSymbol())) {
                toRemove = instrument;
                break;
            }
        }

        if (toRemove == null) {
            return;
        }
        
        instruments.remove(toRemove);
    }

    public double averageRisk() {
        if (instruments.isEmpty()) return 0;

        double totalRiskScore = 0; 
        for (Instrument i : instruments) {
            totalRiskScore += i.riskScore();
        }

        return totalRiskScore / instruments.size();
    }

    public T highestRisk() {
        if (instruments.isEmpty()) return null;

        T maxRiskInstrument = instruments.get(0);

        for (T i : instruments) {
            if (i.riskScore() > maxRiskInstrument.riskScore()) {
                maxRiskInstrument = i; 
            }
        }

        return maxRiskInstrument;
    }

    public T lowestRisk() {
        if (instruments.isEmpty()) return null;

        T minRiskInstrument = instruments.get(0);

        for (T i : instruments) {
            if (i.riskScore() < minRiskInstrument.riskScore()) {
                minRiskInstrument = i; 
            }
        }

        return minRiskInstrument;
    }

    public List<T> getAboveRiskThreshold(double threshold) {
        List<T> safeInstrument = new ArrayList<>();
        
        for (T i : instruments) {
            if (i.riskScore() > threshold) safeInstrument.add(i);
        }

        return safeInstrument;
    }
}
