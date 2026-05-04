package com.fawkes.front.models;

public class RequestItem {
    private int id;
    private RequestProduct product;
    private int quantity;
    private double totalPrice;
    private double unitPrice;

    public RequestItem(int id, RequestProduct product, int quantity, double totalPrice, double unitPrice) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.unitPrice = unitPrice;
    }

    public RequestProduct getProduct() { return product; }
    public double getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }

}
