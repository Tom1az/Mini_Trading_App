import java.util.*;

public class TestRunner {

    // =========================================================
    // HỆ THỐNG ASSERTION (TỰ ĐỘNG BÁO CÁO ĐÚNG/SAI)
    // =========================================================
    private static void assertDoubleEquals(String testName, double expected, double actual) {
        if (Math.abs(expected - actual) < 0.001) {
            System.out.println("[PASS] " + testName);
        } else {
            System.out.println("[FAIL] " + testName + "\n       -> Expected: " + expected + "\n       -> Actual:   " + actual);
        }
    }

    private static void assertStringEquals(String testName, String expected, String actual) {
        if (expected != null && expected.equals(actual)) {
            System.out.println("[PASS] " + testName);
        } else {
            System.out.println("[FAIL] " + testName + "\n       -> Expected: '" + expected + "'\n       -> Actual:   '" + actual + "'");
        }
    }

    private static void assertTrue(String testName, boolean condition, String errorMsg) {
        if (condition) {
            System.out.println("[PASS] " + testName);
        } else {
            System.out.println("[FAIL] " + testName + "\n       -> Error: " + errorMsg);
        }
    }

    // Lớp nội (Inner class) để bắt sự kiện từ Observer
    static class SpyObserver implements Observer<String> {
        String lastEvent = "";
        @Override
        public void onEvent(String event) {
            this.lastEvent = event;
        }
    }

    // Mộc (Mock) PricingStrategy cho bài Test
    static class MockPricingStrategy implements PricingStrategy {
        @Override
        public double calculateFairValue(Instrument instrument) {
            return instrument.getCurrentPriceValue() * 1.1; // Tăng 10%
        }
        @Override
        public String strategyName() {
            return "MockStrategy";
        }
    }

    // =========================================================
    // HÀM MAIN THỰC THI
    // =========================================================
    public static void main(String[] args) {
        System.out.println("====== BẮT ĐẦU CHẠY BỘ TEST ======\n");
        
        // Khởi tạo dữ liệu mẫu
        Stock aapl = new Stock("AAPL", "Apple", 150.0, 3e12, "Tech"); // Risk: 3.0
        Bond us10y = new Bond("US10Y", "Treasury", 1000.0, 5.0, 10);  // Risk: 2.0
        Option callOpt = new Option("OPT", "Call", 10.0, 160.0, true, 30); // Risk: 8.5
        Future goldFut = new Future("GC", "Gold", 2000.0, 100, 30); // Risk: 8.5

        testPartC(aapl, us10y);
        testPartD(aapl, us10y, callOpt, goldFut);
        testPartE(aapl, us10y, callOpt);
        
        System.out.println("\n====== KẾT THÚC BỘ TEST ======");
    }

    // =========================================================
    // PART C: PORTFOLIO MANAGEMENT (PROBLEM 6)
    // =========================================================
    private static void testPartC(Stock aapl, Bond us10y) {
        System.out.println("--- 6. Xử lý Position ---");
        Position posNormal = new Position(aapl, 10, 140.0);
        
        assertDoubleEquals("[Normal] marketValue() tính đúng quantity * currentPrice", 1500.0, posNormal.marketValue()); // 10 * 150
        assertDoubleEquals("[Normal] unrealizedPnL() tính đúng số dương", 100.0, posNormal.unrealizedPnL()); // 1500 - (10 * 140)
        
        Position posZero = new Position(us10y, 0, 1000.0);
        assertDoubleEquals("[Edge Case] unrealizedPnL() với số lượng 0", 0.0, posZero.unrealizedPnL());
        
        posNormal.addQuantity(10, 160.0);
        assertDoubleEquals("[Normal] addQuantity() tính lại Weighted Avg Cost", 150.0, posNormal.getAverageCostBasis()); // ((10*140) + (10*160)) / 20 = 150
        
        posZero.addQuantity(0, 0.0);
        assertDoubleEquals("[Edge Case] addQuantity() qty = 0 không bị lỗi chia 0", 1000.0, posZero.getAverageCostBasis());

        System.out.println("\n--- 7. Quản lý Portfolio và Observer ---");
        Portfolio portfolio = new Portfolio("P1", "Test User");
        SpyObserver spy = new SpyObserver();
        portfolio.addObserver(spy);

        // Test Empty Portfolio
        assertDoubleEquals("[Edge Case] totalMarketValue() khi danh mục rỗng", 0.0, portfolio.totalMarketValue());
        assertDoubleEquals("[Edge Case] totalUnrealizedPnL() khi danh mục rỗng", 0.0, portfolio.totalUnrealizedPnL());

        // Test Add/Merge
        portfolio.addPosition(aapl, 10, 140.0);
        assertTrue("[Normal] addPosition() vị thế mới tạo, danh sách tăng", portfolio.getPositionsSortedByValue().size() == 1, "Size phải là 1");
        assertStringEquals("[Edge Case] addPosition() gửi đúng format thông báo", "ADDED: AAPL x10", spy.lastEvent);

        portfolio.addPosition(aapl, 5, 160.0);
        assertTrue("[Normal] addPosition() gộp vị thế, danh sách KHÔNG tăng", portfolio.getPositionsSortedByValue().size() == 1, "Size vẫn phải là 1");
        assertStringEquals("[Edge Case] addPosition() (Merge) gửi đúng format thông báo", "ADDED: AAPL x5", spy.lastEvent);

        // Test Exceptions & Remove
        boolean caughtGet = false;
        try { portfolio.getPosition("INVALID"); } 
        catch (PositionNotFoundException e) { caughtGet = true; }
        assertTrue("[Edge Case] getPosition() mã sai ném lỗi PositionNotFoundException", caughtGet, "Không ném Exception");

        boolean caughtRemove = false;
        try { portfolio.removePosition("INVALID"); } 
        catch (PositionNotFoundException e) { caughtRemove = true; }
        assertTrue("[Edge Case] removePosition() mã sai ném lỗi PositionNotFoundException", caughtRemove, "Không ném Exception");

        try {
            portfolio.removePosition("AAPL");
            assertTrue("[Normal] removePosition() xóa thành công, danh sách giảm", portfolio.getPositionsSortedByValue().isEmpty(), "Size phải về 0");
            assertStringEquals("[Edge Case] removePosition() gửi đúng format thông báo", "REMOVED: AAPL", spy.lastEvent);
        } catch (Exception e) {
            System.out.println("[FAIL] removePosition() ném lỗi sai bối cảnh");
        }

        // Test Sort & Revalue
        portfolio.addPosition(aapl, 10, 150.0);  // AAPL value: 1500
        portfolio.addPosition(us10y, 5, 1000.0); // US10Y value: 5000
        List<Position> sorted = portfolio.getPositionsSortedByValue();
        assertStringEquals("[Normal] getPositionsSortedByValue() xếp giảm dần đúng", "US10Y", sorted.get(0).getInstrument().getSymbol());

        portfolio.revalueAll(new MockPricingStrategy());
        assertStringEquals("[Edge Case] revalueAll() gửi đúng chuỗi REVALUED", "REVALUED: MockStrategy", spy.lastEvent);
    }

    // =========================================================
    // PART D: RISK ANALYZER (PROBLEM 7)
    // =========================================================
    private static void testPartD(Stock aapl, Bond us10y, Option callOpt, Future goldFut) {
        System.out.println("\n--- 8. Lớp RiskAnalyzer<T> ---");
        RiskAnalyzer<Instrument> analyzer = new RiskAnalyzer<>();

        // Test danh sách rỗng
        assertDoubleEquals("[Edge Case] averageRisk() khi rỗng", 0.0, analyzer.averageRisk());
        assertTrue("[Edge Case] highestRisk() khi rỗng an toàn (trả về null)", analyzer.highestRisk() == null, "Phải trả về null");

        analyzer.add(aapl);   // Risk 3.0
        analyzer.add(us10y);  // Risk 2.0
        analyzer.add(callOpt);// Risk 8.5
        
        assertDoubleEquals("[Normal] averageRisk() tính đúng trung bình", (3.0+2.0+8.5)/3, analyzer.averageRisk());
        assertStringEquals("[Normal] highestRisk() tìm đúng phần tử", "OPT", analyzer.highestRisk().getSymbol());
        assertStringEquals("[Normal] lowestRisk() tìm đúng phần tử", "US10Y", analyzer.lowestRisk().getSymbol());

        // Test nhiều tài sản cùng Risk cao nhất
        analyzer.add(goldFut); // Cũng Risk 8.5
        String highSymbol = analyzer.highestRisk().getSymbol();
        assertTrue("[Edge Case] highestRisk() xử lý khi có nhiều tài sản trùng đỉnh", highSymbol.equals("OPT") || highSymbol.equals("GC"), "Nó trả về mã: " + highSymbol);

        // Test Threshold
        List<Instrument> safeList = analyzer.getAboveRiskThreshold(5.0);
        assertTrue("[Normal] getAboveRiskThreshold() trả đúng số lượng", safeList.size() == 2, "Kỳ vọng 2 (OPT, GC) nhưng ra: " + safeList.size());
        
        List<Instrument> emptyList = analyzer.getAboveRiskThreshold(10.0);
        assertTrue("[Edge Case] getAboveRiskThreshold() rỗng trả về [], KHÔNG null", emptyList != null && emptyList.isEmpty(), "Trả về null hoặc size > 0");
    }

    // =========================================================
    // PART E: VISITOR PATTERN (PROBLEM 8)
    // =========================================================
    private static void testPartE(Stock aapl, Bond us10y, Option callOpt) {
        System.out.println("\n--- 9. Tính thuế với TaxReportVisitor ---");
        
        // Cần set lại giá thủ công cho chắc chắn để dễ test
        aapl.updatePrice(100.0); 
        us10y.updatePrice(1000.0); // Rate 5% -> annual = 50.0
        callOpt.updatePrice(10.0);

        TaxReportVisitor visitor1 = new TaxReportVisitor();
        
        aapl.accept(visitor1);
        assertDoubleEquals("[Normal] visit(Stock) 15% của currentPrice", 15.0, visitor1.getTotalTaxLiability()); // 100 * 0.15
        
        us10y.accept(visitor1);
        assertDoubleEquals("[Normal] visit(Bond) cộng thêm 30% coupon 1 unit", 15.0 + 15.0, visitor1.getTotalTaxLiability()); // Coupon = 50 -> Tax = 15. Total = 30.

        callOpt.accept(visitor1);
        assertDoubleEquals("[Normal] visit(Option) cộng thêm 20% currentPrice", 30.0 + 2.0, visitor1.getTotalTaxLiability()); // 10 * 0.20 = 2. Total = 32.

        // Test rỗng và Reset
        TaxReportVisitor visitorEmpty = new TaxReportVisitor();
        assertDoubleEquals("[Edge Case] getTotalTaxLiability() khi Visitor mới khởi tạo", 0.0, visitorEmpty.getTotalTaxLiability());

        String report = visitor1.getReport();
        assertTrue("[Edge Case] getReport() không bị null và có format", report != null && report.contains("Total Tax"), "Chuỗi report trả về lỗi");
        
        // Test tính độc lập của Instance
        TaxReportVisitor visitorReset = new TaxReportVisitor();
        callOpt.accept(visitorReset);
        assertDoubleEquals("[Edge Case] Instance mới bắt đầu cộng dồn từ 0.0", 2.0, visitorReset.getTotalTaxLiability());
    }
}