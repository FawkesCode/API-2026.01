package com.fawkes.front.models;

import java.text.NumberFormat;
import java.util.Locale;

public class FormProducts {
    private String name;
    private String unityPrice;
    private int quantity;
    private static final NumberFormat CURRENCY =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public FormProducts(String name, double unityPrice, int quantity) {
        this.name = name;
        this.unityPrice = CURRENCY.format(unityPrice);
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public String getUnityPrice() { return unityPrice; }
    public  int getQuantity() { return quantity; };

}
