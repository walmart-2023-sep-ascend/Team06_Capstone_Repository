package com.capstone.OrderService.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "order_sequence")
public class Ordersequence {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    @Id
    private String id;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    private int orderId;
}
