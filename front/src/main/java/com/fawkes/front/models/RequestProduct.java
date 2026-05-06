package com.fawkes.front.models;

public class RequestProduct {
    private int id;
    private String productName;
    private int supplierId;

    public RequestProduct(int id, String productName, int supplierId) {
        this.id = id;
        this.productName = productName;
        this.supplierId = supplierId;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return productName;
    }
    public int getSupplierId() { return supplierId; }
}
