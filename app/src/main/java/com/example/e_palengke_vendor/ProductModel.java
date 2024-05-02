package com.example.e_palengke_vendor;

public class ProductModel {

    private String ProductId;
    private String Category;
    private String Description;
    private String Price;
    private String ProductImg;
    private String ProductName;
    private String Quantity;

    private String OrderId;

    public ProductModel() {
        // Default constructor required for Firebase
    }

    public String getProductId() {
        return ProductId;
    }

    public String getCategory() {
        return Category;
    }

    public String getDescription() {
        return Description;
    }

    public String getPrice() {
        return Price;
    }

    public String getProductImg() {
        return ProductImg;
    }

    public String getProductName() {
        return ProductName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public String getOrderId() {
        return OrderId;
    }
}
