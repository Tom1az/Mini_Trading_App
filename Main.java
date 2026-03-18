public class Main {
    public static void main(String[] args) {
        Stock Apple = new Stock("AAPL", "Apple Inc.", 150.0, 2e12, "Technology");
        Bond US10Y = new Bond("US10Y", "US 10 Year Treasury", 100.0, 1.5, 10);

        System.out.println(Apple);
        Apple.updatePrice(140.0);
        System.out.println(Apple.getLastUpdated());
        System.out.println(US10Y);
        System.out.println(US10Y.getLastUpdated());
    }
}