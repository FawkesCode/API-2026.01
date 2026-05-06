package com.fawkes.front.models;

import java.util.ArrayList;
import java.util.List;

public class RequestSupplier {
    private Integer id;
    private ArrayList<String> paymentMethods;
    private String supplierName;

    public RequestSupplier(Integer id, ArrayList<String> paymentMethods, String supplierName) {
        this.id = id;
        this.paymentMethods = paymentMethods;
        this.supplierName = supplierName;
    }

    public String getSupplierName() { return supplierName; };
    public int getSupplierId() { return id; }


}
