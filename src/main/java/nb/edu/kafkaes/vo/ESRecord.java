package nb.edu.kafkaes.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ESRecord {
    private OrderRecord order;
    private CustomerRecord customer;
    private List<OrderProducts> products;

    public ESRecord() {
    }

    public ESRecord(OrderRecord order, CustomerRecord customer, List<OrderProducts> products) {
        this.order = order;
        this.customer = customer;
        this.products = products;
    }

    public OrderRecord getOrder() {
        return order;
    }

    public CustomerRecord getCustomer() {
        return customer;
    }

    public List<OrderProducts> getProducts() {
        return products;
    }
}
