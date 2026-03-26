import java.util.*;

public class TestRunner {

    // =========================================================
    // HỆ THỐNG MÀU SẮC (ANSI ESCAPE CODES)
    // =========================================================
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";

    // =========================================================
    // HỆ THỐNG ASSERTION
    // =========================================================
    public static void assertEqStr(String testName, String expected, String actual) {
        if (expected != null && expected.equals(actual)) {
            System.out.println(ANSI_GREEN + "[PASS] " + testName + ANSI_RESET);
        } else {
            System.out.println(ANSI_RED + "[FAIL] " + testName + "\n       -> Expected: '" + expected + "', Actual: '" + actual + "'" + ANSI_RESET);
        }
    }

    public static void assertEqDouble(String testName, double expected, double actual, double tol) {
        if (Double.isInfinite(expected) && Double.isInfinite(actual) && Math.signum(expected) == Math.signum(actual)) {
            System.out.println(ANSI_GREEN + "[PASS] " + testName + ANSI_RESET);
            return;
        }
        if (Double.isNaN(expected) && Double.isNaN(actual)) {
            System.out.println(ANSI_GREEN + "[PASS] " + testName + ANSI_RESET);
            return;
        }

        if (Math.abs(expected - actual) <= tol) {
            System.out.println(ANSI_GREEN + "[PASS] " + testName + ANSI_RESET);
        } else {
            System.out.println(ANSI_RED + "[FAIL] " + testName + "\n       -> Expected: " + expected + ", Actual: " + actual + ANSI_RESET);
        }
    }

    public static void assertEqInt(String testName, int expected, int actual) {
        if (expected == actual) {
            System.out.println(ANSI_GREEN + "[PASS] " + testName + ANSI_RESET);
        } else {
            System.out.println(ANSI_RED + "[FAIL] " + testName + "\n       -> Expected: " + expected + ", Actual: " + actual + ANSI_RESET);
        }
    }

    public static void assertEqBool(String testName, boolean expected, boolean actual) {
        if (expected == actual) {
            System.out.println(ANSI_GREEN + "[PASS] " + testName + ANSI_RESET);
        } else {
            System.out.println(ANSI_RED + "[FAIL] " + testName + "\n       -> Expected: " + expected + ", Actual: " + actual + ANSI_RESET);
        }
    }

    public interface Executable {
        void execute() throws Exception;
    }

    public static void assertThrows(String testName, Class<? extends Exception> expectedEx, Executable task) {
        try {
            task.execute();
            System.out.println(ANSI_RED + "[FAIL] " + testName + " | Expected exception: " + expectedEx.getSimpleName() + " but none was thrown." + ANSI_RESET);
        } catch (Exception e) {
            Throwable actualEx = e.getCause() != null ? e.getCause() : e;
            if (expectedEx.isInstance(actualEx) || expectedEx.isInstance(e)) {
                System.out.println(ANSI_GREEN + "[PASS] " + testName + ANSI_RESET);
            } else {
                System.out.println(ANSI_RED + "[FAIL] " + testName + " | Expected exception: " + expectedEx.getSimpleName() + ", Actual: " + actualEx.getClass().getSimpleName() + ANSI_RESET);
            }
        }
    }

    static class SpyObserver implements Observer<String> {
        String lastEvent = "";
        int eventCount = 0; // MỚI THÊM: Biến đếm số lần nhận sự kiện

        @Override
        public void onEvent(String event) {
            this.lastEvent = event;
            this.eventCount++; // MỚI THÊM: Tăng biến đếm mỗi khi nhận được event
        }
    }

    // =========================================================
    // MAIN EXECUTION
    // =========================================================
    public static void main(String[] args) throws Exception {
        System.out.println("====== BẮT ĐẦU CHẠY BỘ TEST (NORMAL: 30%, EDGE: 70%) ======\n");
        testPartA();
        testPartB();
        testPartC();
        testPartD();
        testPartE();
        
        System.out.println("\n====== KIỂM THỬ QUY TRÌNH HỆ THỐNG (E2E WORKFLOW) ======");
        testSystemWorkflow();
        testSystemWorkflowWithEdgeCases();
        
        System.out.println("\n====== KẾT THÚC BỘ TEST ======");
    }

    // =========================================================
    // TEST SUITES
    // =========================================================
    private static void testPartA() {
        System.out.println("\n--- PART A: INSTRUMENT HIERARCHY ---");
        Stock s = new Stock("AAPL", "Apple", 150.0, 3e12, "Tech");
        Bond b = new Bond("US10Y", "Bond", 1000.0, 5.0, 15);
        Option oCall = new Option("OPT", "Call", 10.0, 160.0, true, 30);
        Option oPut = new Option("PUT", "Put", 10.0, 160.0, false, 30);
        Future f = new Future("FUT", "Fut", 50.0, 100, 30);

        assertEqDouble("[Normal] Stock Risk Large", 3.0, s.riskScore(), 0.001);
        assertEqDouble("[Normal] Bond Risk Long", 4.0, b.riskScore(), 0.001);
        assertEqBool("[Normal] Option Call ITM", true, oCall.isInTheMoney(170.0));
        assertEqBool("[Normal] Option Put ITM", true, oPut.isInTheMoney(150.0));
        assertEqStr("[Normal] Future Asset Class", "DERIVATIVE", f.assetClass());

        Stock sEdge1 = new Stock("E1", "Edge", 10.0, 1_000_000_000.0, "Tech"); 
        assertEqDouble("[Edge] Stock exactly 1e9 risk", 5.0, sEdge1.riskScore(), 0.001);
        
        Stock sEdge2 = new Stock("E2", "Edge", 10.0, 10_000_000_000.0, "Tech"); 
        assertEqDouble("[Edge] Stock exactly 10e9 risk", 3.0, sEdge2.riskScore(), 0.001);
        
        Stock sEdge3 = new Stock("E3", "Edge", 10.0, 0.0, "Tech"); 
        assertEqDouble("[Edge] Stock 0 market cap risk", 7.5, sEdge3.riskScore(), 0.001);

        Bond bEdge1 = new Bond("B1", "Edge", 1000.0, 5.0, 10); 
        assertEqDouble("[Edge] Bond exactly 10 yrs risk", 2.0, bEdge1.riskScore(), 0.001);
        
        Bond bEdge2 = new Bond("B2", "Edge", 100.0, 0.0, 5); 
        assertEqDouble("[Edge] Bond 0 coupon payment", 0.0, bEdge2.annualCouponPayment(10), 0.001);

        assertEqBool("[Edge] Option Call exact ATM (spot == strike)", false, oCall.isInTheMoney(160.0));
        assertEqBool("[Edge] Option Put exact ATM (spot == strike)", false, oPut.isInTheMoney(160.0));

        assertThrows("[Edge] Instrument negative price throws", IllegalArgumentException.class, () -> s.updatePrice(-0.01));
        
        try { s.updatePrice(0.0); assertEqDouble("[Edge] Instrument 0.0 price is valid", 0.0, s.getCurrentPriceValue(), 0.001); } 
        catch (Exception e) { System.out.println(ANSI_RED + "[FAIL] Instrument 0.0 price threw exception" + ANSI_RESET); }

        String expectedToString = "Stock[symbol=AAPL, price=0.0, risk=3.0]";
        assertEqStr("[Edge] toString exact format match", expectedToString, s.toString());

        System.out.println("\n--- Bổ sung: Test getTradingInfo ---");
        
        // Test Normal: Giá bình thường (Làm tròn 2 chữ số thập phân)
        Stock sInfoTest = new Stock("AAPL", "Apple Corp", 150.5, 3e12, "Tech");
        // 150.5 phải được format thành 150.50
        String expectedInfoNormal = "AAPL @ 150.50 [AVAILABLE]"; 
        assertEqStr("[Normal] getTradingInfo đúng format chuẩn và làm tròn giá", expectedInfoNormal, sInfoTest.getTradingInfo());
        
        // Test Edge 1: Giá có nhiều lẻ, kiểm tra xem có làm tròn đúng 2 chữ số không
        Stock sInfoRounding = new Stock("TSLA", "Tesla", 120.556, 1e11, "Auto");
        // 120.556 làm tròn thành 120.56
        String expectedInfoRounding = "TSLA @ 120.56 [AVAILABLE]";
        assertEqStr("[Edge] getTradingInfo làm tròn giá đúng quy tắc", expectedInfoRounding, sInfoRounding.getTradingInfo());

        // Test Edge 2: Trạng thái UNAVAILABLE (Giả sử giá = 0.0 thì không cho giao dịch)
        Stock sInfoZero = new Stock("ZERO", "Zero Corp", 0.0, 1e9, "Tech");
        // 0.0 phải được format thành 0.00
        // LƯU Ý: Nếu logic code của bạn quy định giá 0.0 vẫn là [AVAILABLE], hãy tự sửa lại chuỗi bên dưới nhé!
        String expectedInfoZero = "ZERO @ 0.00 [AVAILABLE]"; 
        assertEqStr("[Edge] getTradingInfo xử lý giá 0.0 và trạng thái", expectedInfoZero, sInfoZero.getTradingInfo());
    }

    private static void testPartB() {
        System.out.println("\n--- PART B: INTERFACES ---");
        Stock s = new Stock("GOOG", "Alphabet", 100.0, 2e12, "Tech");
        PricingStrategy simple = new SimplePricingStrategy();
        PricingStrategy riskAdj = new RiskAdjustedPricingStrategy();

        assertEqDouble("[Normal] SimplePricing 5%", 105.0, simple.calculateFairValue(s), 0.001);
        assertEqDouble("[Normal] RiskAdjusted (Risk 3.0 = +3%)", 103.0, riskAdj.calculateFairValue(s), 0.001);
        assertEqDouble("[Normal] getPriceChangePercent", 25.0, ((Priceable)s).getPriceChangePercent(80.0), 0.001);
        assertEqDouble("[Normal] getPriceChangePercent (Price Drop)", -20.0, ((Priceable)s).getPriceChangePercent(125.0), 0.001);

        Stock sZero = new Stock("ZERO", "Zero", 0.0, 2e12, "Tech");
        assertEqDouble("[Edge] SimplePricing on 0.0 price", 0.0, simple.calculateFairValue(sZero), 0.001);
        assertEqDouble("[Edge] RiskAdjusted on 0.0 price", 0.0, riskAdj.calculateFairValue(sZero), 0.001);
        
        assertEqDouble("[Edge] getPriceChange flat price", 0.0, ((Priceable)s).getPriceChange(100.0), 0.001);
        assertEqDouble("[Edge] getPriceChangePercent flat price", 0.0, ((Priceable)s).getPriceChangePercent(100.0), 0.001);
        
        double pctChangeFromZero = ((Priceable)s).getPriceChangePercent(0.0);
        assertEqBool("[Edge] getPriceChangePercent from 0 is Infinity or handled", true, Double.isInfinite(pctChangeFromZero) || pctChangeFromZero == 0.0);
    }

    private static void testPartC() {
        System.out.println("\n--- PART C: PORTFOLIO & OBSERVER ---");
        Stock s = new Stock("TSLA", "Tesla", 200.0, 6e11, "Auto");
        Portfolio p = new Portfolio("P1", "Alice");
        SpyObserver spy = new SpyObserver();
        p.addObserver(spy);

        p.addPosition(s, 10, 180.0);
        
        try {
            assertEqDouble("[Normal] Position marketValue", 2000.0, p.getPosition("TSLA").marketValue(), 0.001);
        } catch (Exception e) {
            System.out.println(ANSI_RED + "[FAIL] [Normal] Position marketValue | Ném lỗi ngoài ý muốn: " + e.getMessage() + ANSI_RESET);
        }
        
        p.addPosition(s, 10, 220.0); 
        assertEqDouble("[Normal] Portfolio total UnrealizedPnL (0)", 0.0, p.totalUnrealizedPnL(), 0.001);

        assertThrows("[Edge] getPosition wrong symbol throws", PositionNotFoundException.class, () -> p.getPosition("WRONG"));
        assertThrows("[Edge] removePosition wrong symbol throws", PositionNotFoundException.class, () -> p.removePosition("WRONG"));

        Position edgePos = new Position(s, 0, 150.0);
        assertEqDouble("[Edge] Position unrealizedPnL with 0 qty", 0.0, edgePos.unrealizedPnL(), 0.001);
        
        edgePos.addQuantity(0, 0.0);
        assertEqDouble("[Edge] addQuantity(0,0) avoids div by zero", 150.0, edgePos.getAverageCostBasis(), 0.001);

        Portfolio pEmpty = new Portfolio("P2", "Bob");
        assertEqDouble("[Edge] Portfolio totalMarketValue when empty", 0.0, pEmpty.totalMarketValue(), 0.001);
        assertEqDouble("[Edge] Portfolio totalUnrealizedPnL when empty", 0.0, pEmpty.totalUnrealizedPnL(), 0.001);
        assertEqInt("[Edge] Portfolio getPositionsSortedByValue empty", 0, pEmpty.getPositionsSortedByValue().size());

        pEmpty.addObserver(spy);
        pEmpty.revalueAll(new SimplePricingStrategy());
        assertEqStr("[Edge] revalueAll sends observer exact message", "REVALUED: SimplePricingStrategy", spy.lastEvent);

        System.out.println("\n--- Bổ sung: Test allocationByAssetClass ---");
        Portfolio pAlloc = new Portfolio("P-ALLOC", "Test Alloc");
        Stock s1 = new Stock("AAPL", "Apple", 100.0, 3e12, "Tech"); 
        Stock s2 = new Stock("MSFT", "Microsoft", 100.0, 2e12, "Tech"); 
        Bond b1 = new Bond("US10Y", "Treasury", 1000.0, 5.0, 10); 
        
        pAlloc.addPosition(s1, 10, 100.0);
        pAlloc.addPosition(s2, 10, 100.0);
        pAlloc.addPosition(b1, 2, 1000.0);
        
        Map<String, Double> alloc = pAlloc.allocationByAssetClass();
        assertEqDouble("[Normal] Allocation cộng dồn nhiều EQUITY chính xác", 50.0, alloc.getOrDefault("EQUITY", 0.0), 0.001);
        assertEqDouble("[Normal] Allocation tính phần trăm FIXED_INCOME chính xác", 50.0, alloc.getOrDefault("FIXED_INCOME", 0.0), 0.001);
        
        Portfolio pEmptyAlloc = new Portfolio("P-EMPTY", "Empty Alloc");
        Map<String, Double> emptyAlloc = pEmptyAlloc.allocationByAssetClass();
        assertEqBool("[Edge] Allocation xử lý an toàn danh mục rỗng (không chia cho 0)", true, emptyAlloc.isEmpty());

        System.out.println("\n--- Bổ sung: Test toString của Position ---");
        
        // Tạo cổ phiếu NVDA giá hiện tại 200.0
        Stock sPosTest = new Stock("NVDA", "Nvidia", 200.0, 2e12, "Tech");
        
        // Tạo Position: Số lượng 5, Giá vốn 150.0
        Position posTest = new Position(sPosTest, 5, 150.0);
        
        // Lắp ráp chuỗi expected theo đúng Spec
        // format: Position[symbol=<symbol>, qty=<quantity>, value=<value>, pnl=<pnl>]
        String expectedPosStr = "Position[symbol=NVDA, qty=5, value=1000.00, pnl=250.00]";
        
        assertEqStr("[Normal] Position toString trả về đúng format và số liệu", expectedPosStr, posTest.toString());
        
        // Edge Case: Lỗ (PnL âm)
        Stock sPosLoss = new Stock("INTC", "Intel", 30.0, 1e11, "Tech");
        Position posLoss = new Position(sPosLoss, 10, 50.0); // Mua giá 50, hiện tại rớt còn 30
        // value = 10 * 30.0 = 300.0
        // pnl = (30.0 - 50.0) * 10 = -200.0
        String expectedLossStr = "Position[symbol=INTC, qty=10, value=300.00, pnl=-200.00]";
        assertEqStr("[Edge] Position toString xử lý đúng PnL âm (Lỗ)", expectedLossStr, posLoss.toString());

        System.out.println("\n--- Bổ sung: Test trùng Observer (Theo Q&A Giảng viên) ---");
        Portfolio pObsTest = new Portfolio("P-OBS", "Test Dupe Observer");
        SpyObserver spyDupe = new SpyObserver();
        
        // Thêm cùng một Observer 2 lần
        pObsTest.addObserver(spyDupe);
        pObsTest.addObserver(spyDupe); // Lần 2: Theo luật giảng viên là phải xóa cũ, thêm mới
        
        // Kích hoạt một sự kiện bằng cách thêm 1 Position
        Stock sObs = new Stock("META", "Meta", 300.0, 1e12, "Tech");
        pObsTest.addPosition(sObs, 10, 300.0);
        
        // Nếu add 2 lần mà list bị duplicate, spyDupe sẽ nhận 2 events (eventCount = 2)
        // Nếu xử lý "xóa cũ thêm mới" đúng, list chỉ có 1, spyDupe nhận 1 event (eventCount = 1)
        assertEqInt("[Edge] Observable xử lý trùng Observer (xóa cũ thêm mới)", 1, spyDupe.eventCount);

        System.out.println("\n--- Bổ sung: Test Trùng Instrument trong Portfolio (Xóa cũ thêm mới) ---");
        Portfolio pDupeTest = new Portfolio("P-DUPE", "Dupe Tester");
        
        // 1. Thêm Instrument lần đầu
        Stock oldStock = new Stock("DUPE", "Old Corp", 100.0, 2e12, "Tech");
        pDupeTest.addPosition(oldStock, 10, 100.0);
        
        // 2. Thêm Instrument MỚI nhưng TRÙNG SYMBOL "DUPE"
        Stock newStock = new Stock("DUPE", "New Corp", 150.0, 2e12, "Tech");
        pDupeTest.addPosition(newStock, 5, 150.0); // Cần gộp thêm 5 quantity
        
        try {
            Position savedPos = pDupeTest.getPosition("DUPE");
            Instrument savedInst = savedPos.getInstrument();
            
            // Kiểm tra: Instrument phải được đè bằng cái mới ("New Corp")
            assertEqStr("[Edge] Portfolio thay thế Instrument cũ bằng mới khi trùng Symbol", "New Corp", savedInst.getName());
            
            // Kiểm tra: Số lượng phải được gộp lại (10 + 5 = 15)
            assertEqInt("[Edge] Portfolio gộp đúng số lượng khi đè Instrument mới", 15, savedPos.getQuantity());
            
        } catch (Exception e) {
            System.out.println(ANSI_RED + "[FAIL] Lỗi lấy Position: " + e.getMessage() + ANSI_RESET);
        }
    }

    private static void testPartD() {
        System.out.println("\n--- PART D: RISK ANALYZER ---");
        RiskAnalyzer<Instrument> ra = new RiskAnalyzer<>();
        Stock s1 = new Stock("S1", "S1", 100.0, 3e12, "Tech"); 
        Stock s2 = new Stock("S2", "S2", 100.0, 5e8, "Tech");  

        ra.add(s1); ra.add(s2);
        assertEqDouble("[Normal] averageRisk", 5.25, ra.averageRisk(), 0.001);
        assertEqStr("[Normal] highestRisk symbol", "S2", ra.highestRisk().getSymbol());
        
        RiskAnalyzer<Instrument> raEmpty = new RiskAnalyzer<>();
        assertEqDouble("[Edge] averageRisk on empty returns 0", 0.0, raEmpty.averageRisk(), 0.001);
        assertEqBool("[Edge] highestRisk on empty is null", true, raEmpty.highestRisk() == null);
        assertEqBool("[Edge] lowestRisk on empty is null", true, raEmpty.lowestRisk() == null);
        
        List<Instrument> emptyThreshold = raEmpty.getAboveRiskThreshold(0.0);
        assertEqBool("[Edge] threshold on empty returns [] not null", true, emptyThreshold != null && emptyThreshold.isEmpty());

        List<Instrument> strictThreshold = ra.getAboveRiskThreshold(7.5); 
        assertEqInt("[Edge] threshold uses strict > (not >=)", 0, strictThreshold.size());

        System.out.println("\n--- Bổ sung: Test Trùng Instrument trong RiskAnalyzer (Xóa cũ thêm mới) ---");
        RiskAnalyzer<Instrument> raDupe = new RiskAnalyzer<>();
        
        // Cổ phiếu rác ban đầu (Vốn hóa nhỏ 1e8) -> RiskScore = 7.5
        Stock oldRiskStock = new Stock("RSK", "Old", 100.0, 1e8, "Tech"); 
        raDupe.add(oldRiskStock);
        
        // Cổ phiếu xịn mới đổi chủ (Vốn hóa lớn 3e12) -> RiskScore = 3.0
        // Cùng mã "RSK"
        Stock newRiskStock = new Stock("RSK", "New", 100.0, 3e12, "Tech"); 
        raDupe.add(newRiskStock);
        
        // NẾU HỆ THỐNG SAI (Giữ cả 2): Average = (7.5 + 3.0) / 2 = 5.25
        // NẾU HỆ THỐNG ĐÚNG (Xóa cũ thêm mới): Average = 3.0 (Chỉ còn lại đúng 1 cái mới)
        assertEqDouble("[Edge] RiskAnalyzer xóa cũ thêm mới khi trùng Symbol", 3.0, raDupe.averageRisk(), 0.001);
    }

    private static void testPartE() {
        System.out.println("\n--- PART E: VISITOR PATTERN ---");
        TaxReportVisitor visitor = new TaxReportVisitor();
        Stock s = new Stock("AAPL", "Apple", 200.0, 3e12, "Tech");
        Bond b = new Bond("US10Y", "Bond", 1000.0, 5.0, 10);
        Option o = new Option("OPT", "Call", 10.0, 160.0, true, 30);

        s.accept(visitor);
        assertEqDouble("[Normal] Stock tax (200 * 15%)", 30.0, visitor.getTotalTaxLiability(), 0.001);
        b.accept(visitor); 
        assertEqDouble("[Normal] Bond tax accumulated (30 + 15)", 45.0, visitor.getTotalTaxLiability(), 0.001);

        TaxReportVisitor emptyVisitor = new TaxReportVisitor();
        assertEqDouble("[Edge] Tax liability on fresh visitor", 0.0, emptyVisitor.getTotalTaxLiability(), 0.001);
        
        Stock sZero = new Stock("ZERO", "Zero", 0.0, 3e12, "Tech");
        sZero.accept(emptyVisitor);
        assertEqDouble("[Edge] Tax on 0.0 priced stock is 0.0", 0.0, emptyVisitor.getTotalTaxLiability(), 0.001);

        o.accept(emptyVisitor); 
        o.accept(emptyVisitor); 
        assertEqDouble("[Edge] Double visit same item accumulates", 4.0, emptyVisitor.getTotalTaxLiability(), 0.001);

        String report = visitor.getReport();
        assertEqBool("[Edge] getReport contains Total", true, report != null && report.contains("Total Tax"));
    }

    // =========================================================
    // HỆ THỐNG WORKFLOW E2E (THEO CHUẨN TÀI LIỆU SPEC)
    // =========================================================
    private static void testSystemWorkflow() {
        System.out.println("\n--- BƯỚC 1: Tạo Instruments ---");
        Stock aapl = new Stock("AAPL", "Apple", 150.0, 3e12, "Tech"); // Risk: 3.0
        Bond us10y = new Bond("US10Y", "US Treasury 10Y", 1000.0, 5.0, 10); // Risk: 2.0
        Option callOpt = new Option("AAPL-C", "Call Option", 10.0, 160.0, true, 30); // Risk: 8.5
        assertEqStr("[Bước 1] Khởi tạo thành công đa hình Asset Class", "EQUITY", aapl.assetClass());

        System.out.println("\n--- BƯỚC 2: Xây dựng Portfolio ---");
        Portfolio p = new Portfolio("P-MAIN", "Manager");
        SpyObserver spy = new SpyObserver();
        p.addObserver(spy);

        p.addPosition(aapl, 100, 150.0);
        assertEqStr("[Bước 2] Danh mục thông báo tự động cho Observer", "ADDED: AAPL x100", spy.lastEvent);

        p.addPosition(us10y, 10, 1000.0);
        p.addPosition(callOpt, 50, 10.0);

        System.out.println("\n--- BƯỚC 3: Thị trường biến động (Revalue) ---");
        PricingStrategy simple = new SimplePricingStrategy();
        p.revalueAll(simple); // Tăng 5%: AAPL=157.5, US10Y=1050.0, OPT=10.5
        
        assertEqStr("[Bước 3] Thông báo Revalue gửi đến Observer", "REVALUED: SimplePricingStrategy", spy.lastEvent);
        assertEqDouble("[Bước 3] Giá AAPL được cập nhật từ Strategy (150 -> 157.5)", 157.5, aapl.getCurrentPriceValue(), 0.001);

        System.out.println("\n--- BƯỚC 4: Phân tích Portfolio ---");
        // Tổng giá trị = (100 * 157.5) + (10 * 1050) + (50 * 10.5) = 15750 + 10500 + 525 = 26775.0
        // Lãi/Lỗ = (15750 - 15000) + (10500 - 10000) + (525 - 500) = 750 + 500 + 25 = 1275.0
        assertEqDouble("[Bước 4] Market Value cộng dồn chính xác", 26775.0, p.totalMarketValue(), 0.001);
        assertEqDouble("[Bước 4] Unrealized PnL phản ánh đúng biến động giá", 1275.0, p.totalUnrealizedPnL(), 0.001);
        
        Map<String, Double> alloc = p.allocationByAssetClass();
        assertEqDouble("[Bước 4] Allocation tính chuẩn % của EQUITY", (15750.0 / 26775.0) * 100, alloc.get("EQUITY"), 0.001);

        RiskAnalyzer<Instrument> ra = new RiskAnalyzer<>();
        ra.add(aapl); ra.add(us10y); ra.add(callOpt);
        assertEqDouble("[Bước 4] RiskAnalyzer quét đúng các Instrument", (3.0 + 2.0 + 8.5)/3.0, ra.averageRisk(), 0.001);

        System.out.println("\n--- BƯỚC 5: Tính thuế cuối năm (Visitor) ---");
        TaxReportVisitor taxVisitor = new TaxReportVisitor();
        
        // Spec nói: "TaxReportVisitor traverses all instruments in the portfolio"
        for (Position pos : p.getPositionsSortedByValue()) {
            pos.getInstrument().accept(taxVisitor);
        }
        
        // Thuế = Cổ phiếu (15% của 157.5 = 23.625) + Trái phiếu (30% của 52.5 coupon = 15.75) + Phái sinh (20% của 10.5 = 2.1)
        // Tổng = 23.625 + 15.75 + 2.1 = 41.475
        assertEqDouble("[Bước 5] Visitor Pattern cộng dồn thuế đúng luật", 41.475, taxVisitor.getTotalTaxLiability(), 0.001);
    }

    // =========================================================
    // HỆ THỐNG WORKFLOW E2E (TÍCH HỢP NHIỀU EDGE CASES)
    // =========================================================
    private static void testSystemWorkflowWithEdgeCases() throws Exception {
        System.out.println("\n--- BƯỚC 1: Tạo Instruments (Kèm Edge Cases) ---");
        // Normal
        Stock aapl = new Stock("AAPL", "Apple", 150.0, 3e12, "Tech");
        Bond us10y = new Bond("US10Y", "US Treasury 10Y", 1000.0, 5.0, 10);
        Option callOpt = new Option("AAPL-C", "Call Option", 10.0, 160.0, true, 30);
        
        // Edge Case: Một cổ phiếu rác có vốn hóa nằm đúng ranh giới risk (1e9) và giá hiện tại là 0.0
        Stock penny = new Stock("PENNY", "Penny Corp", 0.0, 1_000_000_000.0, "Tech");
        assertEqDouble("[Bước 1 - Edge] Penny Stock sát ranh giới 1e9 phải có risk = 5.0", 5.0, penny.riskScore(), 0.001);

        System.out.println("\n--- BƯỚC 2: Xây dựng Portfolio (Kèm Edge Cases) ---");
        Portfolio p = new Portfolio("P-MAIN", "Manager");
        SpyObserver spy = new SpyObserver();
        p.addObserver(spy);

        p.addPosition(aapl, 100, 150.0);
        p.addPosition(us10y, 10, 1000.0);
        p.addPosition(callOpt, 50, 10.0);
        
        // Mua 1000 cổ phiếu rác với giá vốn 5.0/cổ (Tổng vốn = 5000), dù hiện tại trên sàn nó đang là 0.0
        p.addPosition(penny, 1000, 5.0); 

        // Edge Case: Hacker/Lỗi UI cố tình gửi lệnh mua số lượng âm hoặc giá vốn âm
        p.addPosition(aapl, -50, -10.0);
        assertEqInt("[Bước 2 - Edge] Hệ thống phòng thủ tốt, từ chối lệnh số lượng âm", 100, p.getPosition("AAPL").getQuantity());

        System.out.println("\n--- BƯỚC 3: Thị trường biến động (Revalue kèm Edge) ---");
        PricingStrategy simple = new SimplePricingStrategy();
        p.revalueAll(simple); 
        // Sau định giá 5%: AAPL=157.5, US10Y=1050.0, OPT=10.5
        // Edge: PENNY = 0.0 * 1.05 = 0.0
        
        assertEqDouble("[Bước 3 - Edge] Giá Penny stock (0.0) nhân chiến lược 5% vẫn là 0.0", 0.0, penny.getCurrentPriceValue(), 0.001);

        System.out.println("\n--- BƯỚC 4: Phân tích Portfolio (Kèm Edge Cases) ---");
        // Tổng Market Value = AAPL (15750) + US10Y (10500) + OPT (525) + PENNY (0) = 26775.0
        assertEqDouble("[Bước 4 - Normal] Market Value tính chuẩn khi có tài sản giá 0", 26775.0, p.totalMarketValue(), 0.001);
        
        // Lỗ của Penny = MarketValue (0) - Vốn (1000 * 5.0) = -5000.0
        // Tổng PnL = AAPL (+750) + US10Y (+500) + OPT (+25) + PENNY (-5000) = -3725.0
        assertEqDouble("[Bước 4 - Edge] Unrealized PnL gánh khoản lỗ nặng từ Penny Stock", -3725.0, p.totalUnrealizedPnL(), 0.001);

        Map<String, Double> alloc = p.allocationByAssetClass();
        // Cả AAPL (15750) và PENNY (0) đều là EQUITY. Tổng EQUITY = 15750.
        // Tỉ lệ EQUITY = (15750 / 26775) * 100
        assertEqDouble("[Bước 4 - Edge] Allocation gộp chuẩn khi một tài sản cùng loại mất trắng giá trị", (15750.0 / 26775.0) * 100, alloc.get("EQUITY"), 0.001);

        System.out.println("\n--- BƯỚC 5: Tính thuế cuối năm (Kèm Edge Cases) ---");
        TaxReportVisitor taxVisitor = new TaxReportVisitor();
        
        for (Position pos : p.getPositionsSortedByValue()) {
            pos.getInstrument().accept(taxVisitor);
        }
        
        // Thuế = AAPL (15% của 157.5 = 23.625) + US10Y (30% của 52.5 = 15.75) + OPT (20% của 10.5 = 2.1) 
        // Edge: Thuế PENNY = (15% của 0.0 = 0.0)
        // Tổng = 41.475
        assertEqDouble("[Bước 5 - Edge] Thuế của tài sản rác không làm sai lệch tổng hóa đơn thuế", 41.475, taxVisitor.getTotalTaxLiability(), 0.001);
    }
}