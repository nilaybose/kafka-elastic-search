package nb.edu.kafkaes.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderProducts {
    private String id;
    private String orderId;
    private String productId;

    public OrderProducts() {
    }

    public OrderProducts(String id, String orderId, String productId) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getProductId() {
        return productId;
    }
}
