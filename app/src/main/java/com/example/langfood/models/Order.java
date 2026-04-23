package com.example.langfood.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class Order {
    @SerializedName("id")
    private int id;

    @SerializedName("buyerId")
    private String buyerId;

    @SerializedName("buyerName")
    private String buyerName;

    @SerializedName("shipperId")
    private String shipperId;

    @SerializedName("status")
    private String status;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("shippingFee")
    private double shippingFee;

    @SerializedName("createdAt")
    private String createdAt; 

    @SerializedName("deliveredAt")
    private String deliveredAt;

    @SerializedName("deliveryBuilding")
    private String deliveryBuilding;

    @SerializedName("orderItems")
    private List<OrderItem> orderItems;

    @SerializedName("buyer")
    private User buyer;

    public Order() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }

    public String getBuyerName() { return (buyer != null) ? buyer.getFullName() : buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getShipperId() { return shipperId; }
    public void setShipperId(String shipperId) { this.shipperId = shipperId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getShippingFee() { return shippingFee; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(String deliveredAt) { this.deliveredAt = deliveredAt; }

    public String getDeliveryBuilding() { return deliveryBuilding; }
    public void setDeliveryBuilding(String deliveryBuilding) { this.deliveryBuilding = deliveryBuilding; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

    public User getBuyer() { return buyer; }
    public void setBuyer(User buyer) { this.buyer = buyer; }
}
