package nb.edu.kafkaes.vo;

public class KafkaOrderRecord {
    private String orderId;
    private String action;

    public KafkaOrderRecord(String orderId, String action) {
        this.action = action;
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getAction() {
        return action;
    }
}