package com.fawkes.front.models;

import java.text.NumberFormat;
import java.util.Locale;

public class FormProducts {
    private String name;
    private String unityPrice;
    private double unityPriceValue;
    private int quantity;
    private int id;
    private int suppliersId;
    private static final NumberFormat CURRENCY =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public FormProducts(String name, double unityPrice, int quantity, int id, int suppliersId) {
        this.name = name;
        this.unityPrice = CURRENCY.format(unityPrice);
        this.quantity = quantity;
        this.unityPriceValue = unityPrice;
        this.id = id;
        this.suppliersId = suppliersId;
    }

    public String getName() { return name; }
    public String getUnityPrice() { return unityPrice; }
    public double getUnityPriceValue() { return unityPriceValue; }
    public  int getQuantity() { return quantity; };
    public double getTotalValue() { return unityPriceValue * quantity; }
    public int getId() { return id; };
    public int getSuppliersId() { return suppliersId; }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


}
