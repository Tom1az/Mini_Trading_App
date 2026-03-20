public class Option extends Derivative {
    private final double strikePrice;
    private final boolean isCall;
    private final int expiryDays;

    public Option(String symbol, String name, double currentPrice, double strikePrice, boolean isCall, int expiryDays) {
        super(symbol, name, currentPrice);
        this.strikePrice = strikePrice;
        this.isCall = isCall;
        this.expiryDays = expiryDays;
    }

    @Override
    public double riskScore() {
        return 8.5;
    }
    
    public boolean isInTheMoney(double spotPrice) {
        /*
        isCall = true (Quyền chọn Mua): Bạn có quyền mua tài sản với giá niêm yết (strikePrice).
            Khi nào có lời (In the money)? Khi giá thị trường (spotPrice) cao hơn giá niêm yết.
            Ví dụ: Bạn có quyền mua iPhone với giá 20 triệu (strike), trong khi ngoài cửa hàng đang bán 25 triệu (spot). Bạn mua bằng quyền chọn rồi bán lại ra thị trường là có lời ngay 5 triệu.
            => Điều kiện: spotPrice > strikePrice.

        isCall = false (Quyền chọn Bán - Put Option): Bạn có quyền bán tài sản với giá niêm yết (strikePrice).
            Khi nào có lời (In the money)? Khi giá thị trường (spotPrice) thấp hơn giá niêm yết.
            Ví dụ: Bạn có quyền bán vàng với giá 80 triệu/lượng, trong khi giá thị trường sụt xuống còn 70 triệu. Bạn ra chợ mua vàng 70 triệu rồi dùng quyền này để bán với giá 80 triệu. Bạn lời 10 triệu.
            => Điều kiện: spotPrice < strikePrice. 
        */
        return this.isCall ? (spotPrice > this.strikePrice) : (spotPrice < this.strikePrice) ;
    }

    public double getStrikePrice() {
        return this.strikePrice;
    }

    public boolean isCall() {
        return this.isCall;
    }

    public int getExpiryDays() {
        return this.expiryDays;
    }

    @Override
    public void accept(InstrumentVisitor visitor) {
        visitor.visit(this);
    }
}
