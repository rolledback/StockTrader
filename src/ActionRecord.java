import java.sql.Timestamp;

class ActionRecord {

    private String buyerId, stockId, sellerId;
    private Timestamp timestamp;

    public ActionRecord(String buyerId, String stockId, String sellerId) {
        this.buyerId = buyerId;
        this.stockId = stockId;
        this.sellerId = sellerId;
        this.timestamp = new Timestamp(System.currentTimeMillis());;
    }

    public String toString() {
        return buyerId + " bought stock " + stockId + " from " + sellerId;
    }
}
