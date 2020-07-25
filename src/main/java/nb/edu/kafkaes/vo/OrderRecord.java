package nb.edu.kafkaes.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRecord {
    private String orderId;
    private String action;
    private String custId;
    private String total;
    private String tsPlaced;
    private String description;

    public OrderRecord() {
    }

    public OrderRecord(String orderId,
                       String action) {
        this.action = action;
        this.orderId = orderId;
    }

    public OrderRecord(String orderId,
                       String custId,
                       String total,
                       String tsPlaced,
                       String description) {
        this.orderId = orderId;
        this.total = total;
        this.custId = custId;
        this.tsPlaced = tsPlaced;
        this.description = description;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getAction() {
        return action;
    }

    public String getCustId() {
        return custId;
    }

    public String getTotal() {
        return total;
    }

    public String getTsPlaced() {
        return tsPlaced;
    }

    public String getDescription() {
        return description;
    }
}