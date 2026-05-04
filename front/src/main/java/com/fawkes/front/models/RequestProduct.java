package com.fawkes.front.models;

public class RequestProduct {
    private int id;
    private String productName;

    public RequestProduct(int id, String productName) {
        this.id = id;
        this.productName = productName;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return productName;
    }
}
