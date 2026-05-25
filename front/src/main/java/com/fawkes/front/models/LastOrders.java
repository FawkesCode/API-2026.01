package com.fawkes.front.models;

public class LastOrders {
    private String id;
    private String supplier;
    private String solicitor;
    private String value;
    private String status;
    private String deliveryDate;

    public LastOrders(String id, String supplier, String solicitor, String value, String status, String deliveryDate) {
        this.id = id;
        this.supplier = supplier;
        this.solicitor = solicitor;
        this.value = value;
        this.status = status;
        this.deliveryDate = deliveryDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSolicitor() {
        return solicitor;
    }

    public void setSolicitor(String solicitor) {
        this.solicitor = solicitor;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
