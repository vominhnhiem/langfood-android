package com.example.langfood.models;

import com.google.gson.annotations.SerializedName;

public class OrderItem {
    @SerializedName("id")
    private int id;

    @SerializedName("productId")
    private int productId;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("unitPrice")
    private double unitPrice;

    // Backend trả về object Product lồng bên trong
    @SerializedName("product")
    private Product product;

    public OrderItem() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    // Helper methods để lấy data nhanh
    public String getProductName() {
        return (product != null) ? product.getName() : null;
    }

    public String getProductImageUrl() {
        return (product != null) ? product.getImageUrl() : null;
    }
}
