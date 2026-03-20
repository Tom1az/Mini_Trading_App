public class Main {
    public static void main(String[] args) {
        System.out.println("========Test Stock + Bond==========");
        Stock Apple = new Stock("AAPL", "Apple Inc.", 150.0, 2e12, "Technology");
        Bond US10Y = new Bond("US10Y", "US 10 Year Treasury", 100.0, 1.5, 10);

        System.out.println(Apple);
        Apple.updatePrice(140.0);
        System.out.println(Apple.getLastUpdated());
        System.out.println(US10Y);
        US10Y.updatePrice(120.0);
        System.out.println(US10Y.getLastUpdated());

        System.out.println("========Test Option + Value==========");
        Option appleCall = new Option("AAPL_Call", "Apple Call", 150.0, 150.0, true, 30);
        
        System.out.println("Risk Score (Expect 8.5): " + appleCall.riskScore());
        //Nếu isCall = true, nghĩa là quyền mua thì spot > spike là lời, spot < spike là lỗ
        System.out.println("Call In-The-Money (Spot 160, Expect true): " + appleCall.isInTheMoney(160.0));
        //Nếu isCall = false, nghĩa là quyền bán thì spot > spike lỗ, ngược lại là lời
        System.out.println("Call In-The-Money (Spot 140, Expect false): " + appleCall.isInTheMoney(140.0));

        // Tạo một Put Option giá thực hiện 100
        Option goldPut = new Option("GOLD_Put", "Gold Put", 2000.0, 2000.0, false, 60);
        System.out.println("Put In-The-Money (Spot 1900, Expect true): " + goldPut.isInTheMoney(1900.0));

        System.out.println("\n--- TESTING FUTURE ---");
        Future oilFuture = new Future("OIL_Fut", "Crude Oil Future", 70.0, 1000.0, 90);
        System.out.println("Asset Class (Expect DERIVATIVE): " + oilFuture.assetClass());
        System.out.println("Risk Score (Expect 8.5): " + oilFuture.riskScore());

        System.out.println("========Test PricingStrategy==========");
        PricingStrategy strategy;
        strategy = new SimplePricingStrategy();
        System.out.println("Simple " + strategy.calculateFairValue(Apple));
        System.out.println(strategy.strategyName());
        strategy = new RiskAdjustedPricingStrategy();
        System.out.println("RiskAdjusted " + strategy.calculateFairValue(US10Y));
        System.out.println(strategy.strategyName());

        System.out.println("========Test Tradeable + Priceable==========");
        Tradeable appleStock = Apple;
        System.out.println("Apple symbol: " + appleStock.getSymbol());
        System.out.println("Apple current price value: " + appleStock.getCurrentPriceValue());
        System.out.println("Apple's availability for trade: " + appleStock. isAvailableForTrading());
        System.out.println("Apple trading info: " + appleStock.getTradingInfo());

        Priceable us10yBond = US10Y;
        System.out.println("US10Y current price value: " + us10yBond.getCurrentPriceValue());
        System.out.println("US10Y price change: " + us10yBond. getPriceChange(130.0));
        System.out.println("US10Y prive change percent  : " + us10yBond.getPriceChangePercent(130.0));
    }
}