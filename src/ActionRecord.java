import java.sql.Timestamp;

class ActionRecord {

    private String buyerId, stockId, sellerId;
    private int quantitiy;
    private Timestamp timestamp;

    public ActionRecord(String buyerId, String stockId, String sellerId, int quantitiy) {
        this.buyerId = buyerId;
        this.stockId = stockId;
        this.sellerId = sellerId;
        this.quantitiy = quantitiy;
        this.timestamp = new Timestamp(System.currentTimeMillis());;
    }

    public String toString() {
        return buyerId + " bought " + quantitiy + " of stock " + stockId + " from " + sellerId;
    }

    public boolean involvesTrader(String id) {
        return this.buyerId.equals(id) || this.sellerId.equals(id);
    }

    public boolean involvesStock(String id) {
        return this.stockId.equals(id);
    }
}
