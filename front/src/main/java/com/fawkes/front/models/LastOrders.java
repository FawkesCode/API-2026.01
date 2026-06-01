package com.fawkes.front.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LastOrders {
    private int id;
    private String supplierName;
    private String requesterName;
    private double totalValue;
    private String status;
    private String expectedDeliveryDate;

    public int getId() { return id; }
    public String getRequesterName() { return requesterName; }
    public String getSupplierName() { return supplierName; }
    public double getTotalValue() { return totalValue; }
    public String getStatus() { return status; }
    public String getExpectedDeliveryDate() { return expectedDeliveryDate; }

    public void setId(int id) { this.id = id; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public void setTotalValue(double totalValue) { this.totalValue = totalValue; }
    public void setStatus(String statusLabel) { this.status = statusLabel; }
    public void setExpectedDeliveryDate(String expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }
}
